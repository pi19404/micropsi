#include "stdafx.h"

#include "e42/core/SceneGraphInitializer.h"

#include "e42/core/D3DXFrame.h"
#include "e42/core/D3DXMeshContainer.h"
#include "e42/core/SceneGraphRenderer.h"
#include "e42/core/EngineController.h"
#include "e42/core/TextureFactory.h"
#include "e42/core/EffectFactory.h"
#include "e42/core/MeshFactory.h"
#include "e42/core/MeshLoaderOptions.h"

#include "e42/core/D3DXMaterial.h"
#include "e42/core/D3DXEffectDefault.h"
#include "e42/core/D3DXEffectInstance.h"
#include "e42/core/D3DXSemantic.h"
#include "e42/core/D3DVertexElement9.h"

#include "e42/core/ShaderConstants.h"

#include "e42/core/EffectShader.h"

#include "e42/core/SceneGraphIterator.h"

using std::string;

//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXFrame*         CSceneGraphInitializer::ms_pxRootFrame;
CEngineController*  CSceneGraphInitializer::ms_pxEngineController;
CMeshLoaderOptions*	CSceneGraphInitializer::ms_pxMeshLoaderOptions;
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CSceneGraphInitializer::CreateMaterials(CD3DXMeshContainer* pxMeshContainer)
{
	assert(pxMeshContainer->GetD3DXMaterials() && pxMeshContainer->GetMaterials() && pxMeshContainer->GetD3DXEffectInstances());

	CD3DXMaterial* pxD3DXMaterials = pxMeshContainer->GetD3DXMaterials();
	CD3DXEffectInstance* pxD3DXEffectInstance = pxMeshContainer->GetD3DXEffectInstances();

	for (unsigned int iMaterial = 0; iMaterial < pxMeshContainer->GetNumMaterials(); iMaterial++)
	{
		CMaterial* pxMaterial =
			pxMeshContainer->GetMaterials() + iMaterial;

		pxMaterial->Init(pxD3DXEffectInstance[iMaterial].GetEffectFilename(), ms_pxEngineController);

		pxMaterial->AddParameters(pxD3DXEffectInstance[iMaterial].GetDefaults(), pxD3DXEffectInstance[iMaterial].GetNumDefaults());
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CSceneGraphInitializer::ConvertToIndexedBlendedMesh(CD3DXMeshContainer* pMeshContainer)
{
	// Mesh für indexed skinning umwandeln
	CComObjectPtr<ID3DXMesh> spxNewMesh;
	HRESULT hr = pMeshContainer->SkinInfo()->ConvertToIndexedBlendedMesh(
			pMeshContainer->GetMesh().GetPtr(), 
			0,
			pMeshContainer->dwNumPaletteEntries, 
			pMeshContainer->Adjacency(), 
			pMeshContainer->Adjacency(),
			NULL, NULL, 
			&pMeshContainer->dwMaxBoneInfluencesPerVertex, 
			&pMeshContainer->dwNumBoneCombinations,
			&pMeshContainer->spxBoneCombinationBuffer, 
			&spxNewMesh);
	assert(SUCCEEDED(hr));

	pMeshContainer->SetMesh(ms_pxEngineController->GetMeshFactory()->RegisterMesh(spxNewMesh));
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CSceneGraphInitializer::ConvertVertexFormatForIndexedSkinning(CD3DXMeshContainer* pMeshContainer)
{
	DWORD dwNewFVF = 
		pMeshContainer->GetMesh().GetPtr()->GetFVF() //& (D3DFVF_POSITION_MASK | D3DFVF_NORMAL | D3DFVF_TEXCOUNT_MASK)
			| D3DFVF_LASTBETA_UBYTE4;

	if (dwNewFVF != pMeshContainer->GetMesh().GetPtr()->GetFVF())
	{
		CComObjectPtr<ID3DXMesh> spxNewMesh;

		HRESULT hr = pMeshContainer->GetMesh().GetPtr()->CloneMeshFVF(
			pMeshContainer->GetMesh().GetPtr()->GetOptions()/*D3DXMESH_SYSTEMMEM | D3DXMESH_DYNAMIC*/, dwNewFVF, 
			ms_pxEngineController->GetDevice(), &spxNewMesh);

		assert(SUCCEEDED(hr));

		pMeshContainer->SetMesh(ms_pxEngineController->GetMeshFactory()->RegisterMesh(spxNewMesh));
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CSceneGraphInitializer::ConvertVertexDeclarationsForIndexedSkinning(CD3DXMeshContainer* pMeshContainer)
{
	D3DVERTEXELEMENT9 vertexDeclarations[MAX_FVF_DECL_SIZE];       // pointer auf Declarations
	HRESULT hr = pMeshContainer->GetMesh().GetPtr()->GetDeclaration(vertexDeclarations);
	assert(SUCCEEDED(hr));

	int iIdx = 0;
	while (vertexDeclarations[iIdx].Stream != 0xFF) // 0xFF == D3DDECL_END().Stream
	{
		// the vertex shader is expected to interpret the UBYTE4 as a D3DCOLOR, so update the type 
		//   NOTE: this cannot be done with CloneMesh, that would convert the UBYTE4 data to float and then to D3DCOLOR
		//          this is more of a "cast" operation
		if ((vertexDeclarations[iIdx].Usage == D3DDECLUSAGE_BLENDINDICES) && 
			(vertexDeclarations[iIdx].UsageIndex == 0))
		{
			vertexDeclarations[iIdx].Type = D3DDECLTYPE_D3DCOLOR;
		}
		iIdx++;
	}

	hr = pMeshContainer->GetMesh().GetPtr()->UpdateSemantics(vertexDeclarations);
	assert(SUCCEEDED(hr));
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CSceneGraphInitializer::SetupSkinningData(CD3DXMeshContainer* pMeshContainer)
{
	// max. Bones pro Model -> sollte mit FX-File übereinstimmen!
	DWORD dwMaxMatrices = 26;
	pMeshContainer->dwNumPaletteEntries = min(dwMaxMatrices, pMeshContainer->SkinInfo()->GetNumBones());

	pMeshContainer->bUsesSoftwareVertexProcessing = 
		!(ms_pxEngineController->GetDeviceCaps()->VertexShaderVersion >= D3DVS_VERSION(1, 1));

	ConvertToIndexedBlendedMesh(pMeshContainer);
	ConvertVertexFormatForIndexedSkinning(pMeshContainer);
	ConvertVertexDeclarationsForIndexedSkinning(pMeshContainer);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
bool
CSceneGraphInitializer::ReadSemantic(const char* pcSemantic, const char* pcUsage, D3DDECLUSAGE usage, D3DXSEMANTIC* pxSemantic)
{
	if (strcmp(pcSemantic, pcUsage) == 0)
	{
		pxSemantic->Usage = usage;
		pxSemantic->UsageIndex = 0;
		return true;
	}

	if (sscanf(pcSemantic + strlen(pcUsage), "%d", &pxSemantic->UsageIndex) == 1)
	{
		pxSemantic->Usage = usage;
		return true;
	}

	return false;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
bool
CSceneGraphInitializer::GetSemanticFromString(const char* pcSemantic, D3DXSEMANTIC* pxSemantic)
{
	if (pcSemantic == NULL) return false;

	return 
		ReadSemantic(pcSemantic, SHADERCONSTANT_SEMANTIC_NORMAL, D3DDECLUSAGE_NORMAL, pxSemantic) ||
		ReadSemantic(pcSemantic, SHADERCONSTANT_SEMANTIC_TANGENT, D3DDECLUSAGE_TANGENT, pxSemantic) ||
		ReadSemantic(pcSemantic, SHADERCONSTANT_SEMANTIC_BINORMAL, D3DDECLUSAGE_BINORMAL, pxSemantic) ||
		ReadSemantic(pcSemantic, SHADERCONSTANT_SEMANTIC_TEXCOORD, D3DDECLUSAGE_TEXCOORD, pxSemantic) ||
		ReadSemantic(pcSemantic, SHADERCONSTANT_SEMANTIC_COLOR, D3DDECLUSAGE_COLOR, pxSemantic) ||
		ReadSemantic(pcSemantic, SHADERCONSTANT_SEMANTIC_POSITION, D3DDECLUSAGE_POSITION, pxSemantic);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CSceneGraphInitializer::ReadShaderInputElements(CD3DXMeshContainer* pxMeshContainer, CDynArray<CD3DXSemantic>* paxRequiredSemantics)
{
	for (int iMaterialID = 0; iMaterialID < (int)pxMeshContainer->GetNumMaterials(); iMaterialID++)
	{
		const CMaterial& rxMaterial = pxMeshContainer->GetMaterials()[iMaterialID];

		TEffectHandle hndEffect = rxMaterial.GetEffect();
		ID3DXEffect* pd3dEffect = hndEffect->GetD3DXEffect();

		D3DXEFFECT_DESC xEffectDescription;
		pd3dEffect->GetDesc(&xEffectDescription);


		for (int iParamIdx = 0; iParamIdx < (int)xEffectDescription.Parameters; iParamIdx++)
		{
			D3DXHANDLE hndParam = pd3dEffect->GetParameter(NULL, iParamIdx);

			D3DXPARAMETER_DESC xParameterDesc;
			pd3dEffect->GetParameterDesc(hndParam, &xParameterDesc);

			if ((xParameterDesc.Name && strcmp(xParameterDesc.Name, "E42_REQUIRE_INPUT") == 0) ||
				(xParameterDesc.Semantic && strcmp(xParameterDesc.Semantic, "E42_REQUIRE_INPUT") == 0))
			{
				for (int iMemberIdx = 0; iMemberIdx < (int)xParameterDesc.StructMembers; iMemberIdx++)
				{
					D3DXHANDLE hndMember = pd3dEffect->GetParameter(hndParam, iMemberIdx);

					D3DXPARAMETER_DESC xMemberDesc;
					pd3dEffect->GetParameterDesc(hndMember, &xMemberDesc);

					if (xMemberDesc.Semantic)
					{
						D3DXSEMANTIC xSemantic;
						if (GetSemanticFromString(xMemberDesc.Semantic, &xSemantic))
						{
							paxRequiredSemantics->Push() = xSemantic;
						}
					}
				}
				break;
			}
		}
	}

	paxRequiredSemantics->RemoveDuplicates(); 
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CSceneGraphInitializer::AddVertexComponents(CD3DXMeshContainer* pxMeshContainer, int iUsageIdx, 
											bool bRequireNormals, bool bRequireTangents, bool bRequireBinormals, 
											bool bRequireTexCoord, bool bRequirePosition, bool bRequireColor)
{
	if (!bRequireNormals && !bRequireTangents && !bRequireBinormals &&
		!bRequireTexCoord && !bRequirePosition && !bRequireColor)
	{
		return;
	}

	HRESULT hr;
	
	// 1.) exisitierende Vertexkomponenten suchen
	D3DVERTEXELEMENT9* pxVertexElements = new D3DVERTEXELEMENT9[MAX_FVF_DECL_SIZE];
	
	hr = pxMeshContainer->GetMesh()->GetDeclaration(pxVertexElements);
	assert(SUCCEEDED(hr));


	int iNormalElementIdx = -1;
	int iTangentElementIdx = -1;
	int iBinormalElementIdx = -1;
	int iTexCoordElementIdx = -1;
	int iPositionElementIdx = -1;
	int iColorElementIdx = -1;
	
	int iElementIdx = 0;
	
	while (pxVertexElements[iElementIdx].Stream != 0xFF)
	{
		if (pxVertexElements[iElementIdx].UsageIndex == iUsageIdx)
		{
			switch (pxVertexElements[iElementIdx].Usage)
			{
			case D3DDECLUSAGE_NORMAL :      iNormalElementIdx = iElementIdx; break;
			case D3DDECLUSAGE_TANGENT :     iTangentElementIdx = iElementIdx; break;
			case D3DDECLUSAGE_BINORMAL :    iBinormalElementIdx = iElementIdx; break;
			case D3DDECLUSAGE_TEXCOORD :    iTexCoordElementIdx = iElementIdx; break;
			case D3DDECLUSAGE_POSITION :    iPositionElementIdx = iElementIdx; break;
			case D3DDECLUSAGE_COLOR :       iColorElementIdx = iElementIdx; break;
			}
		}
	    
		iElementIdx++;
	}


	// 2.) Komponenten "abwählen", die schon existieren
	if (iNormalElementIdx != -1) bRequireNormals = false;
	if (iTangentElementIdx != -1) bRequireTangents = false;
	if (iBinormalElementIdx != -1) bRequireBinormals = false;
	if (iTexCoordElementIdx != -1) bRequireTexCoord = false;
	if (iPositionElementIdx != -1) bRequirePosition = false;
	if (iColorElementIdx != -1) bRequireColor = false;


	// 3.) Komponenten hinzufügen
	if (bRequireNormals || bRequireTangents || bRequireBinormals)
	{
		assert(iTexCoordElementIdx != -1);

		// 3.a) VertexDeklaration erweitern
		int iLastElement = iElementIdx;
		int iVertexSize = pxMeshContainer->GetMesh()->GetNumBytesPerVertex();
		bool bDeclarationChanged = false;
	    
		if (iNormalElementIdx == -1)
		{
			iNormalElementIdx = iLastElement;
			pxVertexElements[iNormalElementIdx] = 
				CD3DVertexElement9(0, iVertexSize, D3DDECLTYPE_FLOAT3, D3DDECLMETHOD_DEFAULT, D3DDECLUSAGE_NORMAL, iUsageIdx);
	        
			iVertexSize += 12;
			iLastElement++;
			bDeclarationChanged = true;
		}
	    
		if (iTangentElementIdx == -1)
		{
			iTangentElementIdx = iLastElement;
			pxVertexElements[iTangentElementIdx] = 
				CD3DVertexElement9(0, iVertexSize, D3DDECLTYPE_FLOAT3, D3DDECLMETHOD_DEFAULT, D3DDECLUSAGE_TANGENT, iUsageIdx);
	        
			iVertexSize += 12;
			iLastElement++;
			bDeclarationChanged = true;
		}
	    
		if (iBinormalElementIdx == -1)
		{
			iBinormalElementIdx = iLastElement;
			pxVertexElements[iBinormalElementIdx] = 
				CD3DVertexElement9(0, iVertexSize, D3DDECLTYPE_FLOAT3, D3DDECLMETHOD_DEFAULT, D3DDECLUSAGE_BINORMAL, iUsageIdx);
	        
			iVertexSize += 12;
			iLastElement++;
			bDeclarationChanged = true;
		}
	    
		pxVertexElements[iLastElement] = CD3DVertexElement9::D3DDECL_END;
	    
	    
		// 3.b) Mesh-Vertexbuffer konvertieren
		CComObjectPtr<ID3DXMesh> spxNewMesh;
	    
		if (bDeclarationChanged)
		{
			pxMeshContainer->GetMesh()->CloneMesh(
				pxMeshContainer->GetMesh()->GetOptions(),
				pxVertexElements,
				ms_pxEngineController->GetDevice(),
				&spxNewMesh);
			assert(SUCCEEDED(hr));
		}
		else
		{
			spxNewMesh = pxMeshContainer->GetMesh().GetPtr();
		}
	    
		delete [] pxVertexElements;
	    

		// 3.c) Tangenten etc. berechnen
		CComObjectPtr<ID3DXMesh> spxTmpMesh = spxNewMesh;
		hr = D3DXComputeTangentFrameEx(
			spxTmpMesh,
			D3DDECLUSAGE_TEXCOORD, iUsageIdx, 
			bRequireTangents ? D3DDECLUSAGE_TANGENT : D3DX_DEFAULT, iUsageIdx,
			bRequireBinormals ? D3DDECLUSAGE_BINORMAL : D3DX_DEFAULT, iUsageIdx,
			bRequireNormals ? D3DDECLUSAGE_NORMAL : D3DX_DEFAULT, iUsageIdx,
			D3DXTANGENT_DONT_ORTHOGONALIZE | D3DXTANGENT_WRAP_UV,
			pxMeshContainer->Adjacency(),
			0.25f, 0.25f, 0.25f,
			&spxNewMesh,
			NULL);
		assert(SUCCEEDED(hr));
	    
		pxMeshContainer->SetMesh(ms_pxEngineController->GetMeshFactory()->RegisterMesh(spxNewMesh));

		bRequireNormals = false;
		bRequireTangents = false;
		bRequireBinormals = false;
	}

	assert(bRequireNormals || bRequireTangents || bRequireBinormals ||
		bRequireTexCoord || bRequirePosition || bRequireColor == false);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CSceneGraphInitializer::SetupVertexStreams(CD3DXMeshContainer* pxMeshContainer)
{
	CDynArray<CD3DXSemantic> axRequiredSemantics;

	if (ms_pxMeshLoaderOptions->m_bGenerateVertexElementsByShaderInput &&
		ms_pxMeshLoaderOptions->m_bLoadResources)
	{
		ReadShaderInputElements(pxMeshContainer, &axRequiredSemantics);
	}

	if (ms_pxMeshLoaderOptions->m_bGenerateNormals)
	{
		axRequiredSemantics.Push() = CD3DXSemantic(D3DDECLUSAGE_NORMAL, 0);
	}

	if (ms_pxMeshLoaderOptions->m_bGenerateTangents)
	{
		axRequiredSemantics.Push() = CD3DXSemantic(D3DDECLUSAGE_TANGENT, 0);
	}

	if (ms_pxMeshLoaderOptions->m_bGenerateBinormals)
	{
		axRequiredSemantics.Push() = CD3DXSemantic(D3DDECLUSAGE_BINORMAL, 0);
	}


	axRequiredSemantics.RemoveDuplicates();


	for (int iUsageIdx = 0; iUsageIdx < 8; iUsageIdx++)
	{
		bool bRequireNormals = axRequiredSemantics.Find(CD3DXSemantic(D3DDECLUSAGE_NORMAL, iUsageIdx)) != -1;
		bool bRequireTangents = axRequiredSemantics.Find(CD3DXSemantic(D3DDECLUSAGE_TANGENT, iUsageIdx)) != -1;
		bool bRequireBinormals = axRequiredSemantics.Find(CD3DXSemantic(D3DDECLUSAGE_BINORMAL, iUsageIdx)) != -1;
		bool bRequireTexCoord = axRequiredSemantics.Find(CD3DXSemantic(D3DDECLUSAGE_TEXCOORD, iUsageIdx)) != -1;
		bool bRequirePosition = axRequiredSemantics.Find(CD3DXSemantic(D3DDECLUSAGE_POSITION, iUsageIdx)) != -1;
		bool bRequireColor = axRequiredSemantics.Find(CD3DXSemantic(D3DDECLUSAGE_COLOR, iUsageIdx)) != -1;

		AddVertexComponents(pxMeshContainer, iUsageIdx, 
			bRequireNormals, bRequireTangents, bRequireBinormals, 
			bRequireTexCoord, bRequirePosition, bRequireColor);
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CSceneGraphInitializer::SetupShaderTextures(CD3DXMeshContainer* pxMeshContainer)
{
	for (int iMaterialID = 0; iMaterialID < (int)pxMeshContainer->GetNumMaterials(); iMaterialID++)
	{
		const CMaterial& rxMaterial = pxMeshContainer->GetMaterials()[iMaterialID];

		TEffectHandle hndEffect = rxMaterial.GetEffect();
		ID3DXEffect* pd3dEffect = hndEffect->GetD3DXEffect();

		D3DXEFFECT_DESC xEffectDescription;
		pd3dEffect->GetDesc(&xEffectDescription);


		for (int iParamIdx = 0; iParamIdx < (int)xEffectDescription.Parameters; iParamIdx++)
		{
			D3DXHANDLE hndParam = pd3dEffect->GetParameter(NULL, iParamIdx);

			D3DXPARAMETER_DESC xParameterDesc;
			pd3dEffect->GetParameterDesc(hndParam, &xParameterDesc);

			if (xParameterDesc.Type == D3DXPT_TEXTURE ||
				xParameterDesc.Type == D3DXPT_TEXTURE2D ||
				xParameterDesc.Type == D3DXPT_TEXTURECUBE)
			{
				D3DXHANDLE hndFilename = pd3dEffect->GetAnnotationByName(hndParam, "SasResourceAddress");

				const char* pcFilename = NULL;
				if (hndFilename != NULL &&
					SUCCEEDED(pd3dEffect->GetString(hndFilename, &pcFilename)))
				{
					TTextureHandle hndTexture = ms_pxEngineController->GetTextureFactory()->CreateTextureFromFile(string("xfl-texture>") + pcFilename);
					pxMeshContainer->GetMaterials()[iMaterialID].SetTexture(xParameterDesc.Name, hndTexture);
				}
			}
		}
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CSceneGraphInitializer::CleanMesh(CD3DXMeshContainer* pxMeshContainer)
{
	CComObjectPtr<ID3DXMesh> spxCleanedMesh;
	CComObjectPtr<ID3DXBuffer> spxErrorsAndWarnings;

	D3DXCleanMesh(
		D3DXCLEAN_SIMPLIFICATION,
		pxMeshContainer->GetMesh().GetPtr(),
		pxMeshContainer->Adjacency(),
		&spxCleanedMesh,
		pxMeshContainer->Adjacency(),
		&spxErrorsAndWarnings);

	if (spxErrorsAndWarnings)
	{
		MessageBox(NULL, (char*)spxErrorsAndWarnings->GetBufferPointer(), "D3DXCleanMesh", 0);
	}


	pxMeshContainer->SetMesh(ms_pxEngineController->GetMeshFactory()->RegisterMesh(spxCleanedMesh));
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CSceneGraphInitializer::SimplifyMesh(CD3DXMeshContainer* pxMeshContainer, float fFactor)
{
	assert(fFactor > 0 && fFactor <= 1);

	CComObjectPtr<ID3DXMesh> spxSimplifiedMesh;
	int iNumFaces = (int)(fFactor * pxMeshContainer->GetMesh()->GetNumFaces());
	if (iNumFaces < 2) iNumFaces = 2;

	HRESULT hr =
		D3DXSimplifyMesh(
			pxMeshContainer->GetMesh().GetPtr(),
			pxMeshContainer->Adjacency(),
			NULL, NULL,
			iNumFaces,
			D3DXMESHSIMP_FACE,
			&spxSimplifiedMesh);
	assert(SUCCEEDED(hr));

	pxMeshContainer->DeleteAdjacencyArray();

	pxMeshContainer->SetMesh(ms_pxEngineController->GetMeshFactory()->RegisterMesh(spxSimplifiedMesh));

	pxMeshContainer->CreateAdjacencyArray();		// (mit neuem IndexCount erzeugen)
	spxSimplifiedMesh->GenerateAdjacency(0, pxMeshContainer->Adjacency());
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CSceneGraphInitializer::CalcBoundingSphere(CD3DXFrame* pxFrame)
{
	// eigene Transformationsmatrix vorrübergehend auf Identity setzen
	CMat4S matFrameTransformationMatrix = pxFrame->TransformationMatrix();
	pxFrame->TransformationMatrix().SetIdentity();

	HRESULT hr = D3DXFrameCalculateBoundingSphere(
		&pxFrame->D3DXFrame(), 
		(D3DXVECTOR3*)&(pxFrame->xBoundingSphere.m_vCenter),
		&(pxFrame->xBoundingSphere.m_fRadius));

	assert(SUCCEEDED(hr));

	// alte Matrix wiederherstellen
	pxFrame->TransformationMatrix() = matFrameTransformationMatrix;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CSceneGraphInitializer::InitVisibilitySwitchFrame(CD3DXFrame* pxFrame)
{
	pxFrame->pVisibilitySwitchFrame = NULL;

	CD3DXFrame* pxChildFrame = pxFrame->GetFirstChild();
	while (pxChildFrame)
	{
		if (pxChildFrame->GetName() &&
			strstr(pxChildFrame->GetName(), "visibility"))
		{
			pxFrame->pVisibilitySwitchFrame = pxChildFrame;
			break;
		}
		pxChildFrame = pxChildFrame->GetSibling();
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CSceneGraphInitializer::RecursiveSetupParentPointers(CD3DXFrame* pxFrame)
{
	if (pxFrame)
	{
		pxFrame->UpdateChildFrameParentPointers();
		pxFrame->UpdateChildMeshParentPointers();

		RecursiveSetupParentPointers(pxFrame->GetSibling());
		RecursiveSetupParentPointers(pxFrame->GetFirstChild());
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CSceneGraphInitializer::CreateEdgeQuads(CD3DXMeshContainer* pxMeshContainer, float fShadowVolumeShrink)
{
	assert(pxMeshContainer->UsesSkinning() == false && "skinned shadowmeshes are currently not supported");
	assert((pxMeshContainer->GetMesh()->GetOptions() & D3DXMESH_32BIT) == 0 && "32 bit index buffers are currently not supported!");

	TMeshHandle hndSrcMesh = pxMeshContainer->GetMesh();

	CComObjectPtr<IDirect3DDevice9> spxDevice;
	hndSrcMesh->GetDevice(&spxDevice);

	// im Vertex-Format des Source-Meshes nach dem Offset der Position suchen
	int iSrcVertexSize = hndSrcMesh->GetNumBytesPerVertex();
	int iSrcPositionOffset = pxMeshContainer->CalcVertexElementOffset(D3DDECLUSAGE_POSITION);
	int iSrcNormalOffset = pxMeshContainer->CalcVertexElementOffset(D3DDECLUSAGE_NORMAL);

	assert(iSrcPositionOffset != -1 && "mesh's vertex stream doesn't contain positions");
	assert((iSrcNormalOffset != -1 || fShadowVolumeShrink == 0) && "mesh's vertex stream doesn't contain normals");


	// VertexFormat des Destination-Meshes anlegen
	struct ShadowMeshVertex
	{
		CVec3 vPos;
		CVec3 vNrm;
	};


	// Source-Mesh zum Lesen locken 
	unsigned short* pSrcIndices;
	char* pSrcVertices;
	hndSrcMesh->LockIndexBuffer(D3DLOCK_READONLY, (void**)&pSrcIndices);
	hndSrcMesh->LockVertexBuffer(D3DLOCK_READONLY, (void**)&pSrcVertices);

	DWORD* pSrcAdjacency = pxMeshContainer->Adjacency();


	// Index- und VertexArrays für ShadowMesh anlegen
	unsigned short* pDstIndices;
	ShadowMeshVertex* pDstVertices;
	pDstIndices = new unsigned short[hndSrcMesh->GetNumFaces() * 4 * 3];		// maximal 4x mehr Faces als vorher
	pDstVertices = new ShadowMeshVertex[hndSrcMesh->GetNumFaces() * 3];		// maximal soviel Vertices wie Faces*3

	assert(hndSrcMesh->GetNumFaces() * 3 < 65536);	// kontrollieren, ob neues Mesh noch mit 16 Bit-Indices darstellbar ist (hinreichende Bedingung)


	// Vertices des Shadowmeshs generieren:
	//   dazu alle Vertices mit Face-Normals versehen
	//   Vertices müssen dazu gesplittet werden
	//   es erfolgt eine 1:1-Zuweisung von Faces zu Vertices
	for (int iFace = 0; iFace < (int)hndSrcMesh->GetNumFaces(); iFace++)
	{
		const CVec3 vCornerPos[] =
		{
			*(const CVec3*)(pSrcVertices + (pSrcIndices[iFace * 3 + 0] * iSrcVertexSize + iSrcPositionOffset)),
			*(const CVec3*)(pSrcVertices + (pSrcIndices[iFace * 3 + 1] * iSrcVertexSize + iSrcPositionOffset)),
			*(const CVec3*)(pSrcVertices + (pSrcIndices[iFace * 3 + 2] * iSrcVertexSize + iSrcPositionOffset))
		};

		const CVec3 vFace1 = vCornerPos[0] - vCornerPos[1];
		const CVec3 vFace2 = vCornerPos[0] - vCornerPos[2];

		CVec3 vFaceNormal = vFace1 ^ vFace2;
		vFaceNormal.Normalize();

		for (int iCorner = 0; iCorner < 3; iCorner++)
		{
			int iCornerIdx = iFace * 3 + iCorner;

			pDstVertices[iCornerIdx].vPos = vCornerPos[iCorner];
			pDstVertices[iCornerIdx].vNrm = vFaceNormal;
			pDstIndices[iCornerIdx] = iCornerIdx;

			if (iSrcNormalOffset != -1)
			{
				// VertexNormale abziehen um Volumen zu verkleinern
				const CVec3& vVertexNormal = *(const CVec3*)(pSrcVertices + (iSrcVertexSize * pSrcIndices[iCornerIdx] + iSrcNormalOffset));
				pDstVertices[iCornerIdx].vPos -= vVertexNormal * fShadowVolumeShrink;
			}
		}
	}
	// VertexArray des ShadowMeshes enthält nun dreimal soviel Vertices wie das SrcMesh Faces hat
	// IndexArray des ShadowMeshes enthält gleiche Anzahl von Indices


	// CounterVatiable für ShadowMesh-Indices anlegen und mit aktuellem Wert initialisieren
	int iDstIndexCount = hndSrcMesh->GetNumFaces() * 3;


	// Edges der Dreiecke werden nun durch Quads verbunden
	//   dabei werden neue Faces generiert
	//   Iteration über Faces erfolgt bis zum Index des letzten Faces im OriginalMesh
	for (int iFace1 = 0; iFace1 < (int)hndSrcMesh->GetNumFaces(); iFace1++)
	{
		// Iteration über alle Edges dieses Dreiecks
		for (int iEdge1 = 0; iEdge1 < 3; iEdge1++)
		{
			// Indices der Edge ermitteln 
			// (LookUp nicht nötig wegen 1:1-Beziehung im IndexArray)
			int iIndexStart1 =		iFace1 * 3 + (iEdge1 + 0) % 3; // pDstIndices[iFace1 * 3 + (iEdge1 + 0) % 3];
			int iIndexEnd1 =		iFace1 * 3 + (iEdge1 + 1) % 3; // pDstIndices[iFace1 * 3 + (iEdge1 + 1) % 3];

			// Vertices der Edge ermitteln
			#ifdef _DEBUG
			const CVec3& vStart1 =	pDstVertices[iIndexStart1].vPos;
			const CVec3& vEnd1 =	pDstVertices[iIndexEnd1].vPos;
			#endif // _DEBUG
			const CVec3& vNormal1 =	pDstVertices[iIndexEnd1].vNrm;


			// über alle angrenzenden Dreiecke des SourceMeshes iterieren 
			// (DreiecksIndices im SourceMesh sind dieselben wie im DestMesh)
			//		optimaler wäre es natürlich vom EdgeIndex direkt auf den AdjacentFaceIndex schließen zu können
			//		allerdings ist in der Doku kein Zusammenhang zwischen Index- und Adjacency-Buffer dokumentiert

			//for (int iAdjacentFaceIdx = 0; iAdjacentFaceIdx < 3; iAdjacentFaceIdx++)
			int iAdjacentFaceIdx = iEdge1;	// funktioniert, ist aber nicht dokumentiert
			{
				// Index des angrenzenden Dreiecks ermitteln
				int iFace2 = pSrcAdjacency[iFace1 * 3 + iAdjacentFaceIdx];	// Reihenfolge der Faces blieb unverändert - daher kann SrcAdjacency verwendet werden

				assert(iFace2 != -1 && "geometry is not closed!");

				// die Edge nur betrachten, wenn der FaceIndex des angrenzenden Faces höher ist
				// -> verhindert, dass die Quads von jeder Seite der Edge einmal eingefügt werden und verlangt das iFace != -1
				if (iFace2 > iFace1)
				{
					bool bEdgeWasFound = false;

					// über die Edges des angrenzenden Dreiecks iterieren
					for (int iEdge2 = 0; iEdge2 < 3; iEdge2++)
					{
						if (pSrcAdjacency[iFace2 * 3 + iEdge2] == iFace1)
						{
							// Indices der Edge ermitteln 
							// (LookUp nicht nötig wegen 1:1-Beziehung im IndexArray)
							int iIndexStart2 =		iFace2 * 3 + (iEdge2 + 0) % 3;	//pDstIndices[iFace2 * 3 + (iEdge2 + 0) % 3];
							int iIndexEnd2 =		iFace2 * 3 + (iEdge2 + 1) % 3;	//pDstIndices[iFace2 * 3 + (iEdge2 + 1) % 3];

							// Vertices der Edge ermitteln
							#ifdef _DEBUG
							const CVec3& vStart2 =	pDstVertices[iIndexStart2].vPos;
							const CVec3& vEnd2 =	pDstVertices[iIndexEnd2].vPos;
							#endif // _DEBUG
							const CVec3& vNormal2 =	pDstVertices[iIndexEnd2].vNrm;


							// prüfen ob die Edges beider Dreiecke übereinstimmen
							assert(vStart1 == vEnd2 && vEnd1 == vStart2);

							bEdgeWasFound = true;

							// Quad muss nur eingefügt werden, wenn abweichende Normalen vorliegen
							const CVec3 vNormalDiff = vNormal1 - vNormal2;
							float fNormalDiff = fabsf(vNormalDiff.x()) + fabsf(vNormalDiff.y()) + fabsf(vNormalDiff.z());

							if (fNormalDiff > 0)
							{
								// Indices für das Quad hinzufügen
								pDstIndices[iDstIndexCount + 0] = iIndexEnd1;
								pDstIndices[iDstIndexCount + 1] = iIndexStart1;
								pDstIndices[iDstIndexCount + 2] = iIndexStart2;

								pDstIndices[iDstIndexCount + 3] = iIndexStart2;
								pDstIndices[iDstIndexCount + 4] = iIndexStart1;
								pDstIndices[iDstIndexCount + 5] = iIndexEnd2;

								iDstIndexCount += 6;
							}

							break;
						}
					}

					assert(bEdgeWasFound); // gesuchte Edge wurde im Nachbardreieck nicht gefunden - evtl. ist die Annahme über die Reihenfolge der Adjacency-Daten nicht korrekt
				}
			}
		}
	}


	// Zugriff auf SourceMesh-Buffer beenden
	hndSrcMesh->UnlockIndexBuffer();
	hndSrcMesh->UnlockVertexBuffer();


	// ShadowMesh anlegen
	CComObjectPtr<ID3DXMesh> spxDstMesh;
	HRESULT hr = D3DXCreateMeshFVF(
		iDstIndexCount / 3,					// Faces = Anzahl generierter Indices / 3
		hndSrcMesh->GetNumFaces() * 3,		// Vertices = 3 * Anzahl Faces im SourceMesh
		hndSrcMesh->GetOptions(),
		D3DFVF_XYZ | D3DFVF_NORMAL,			// VertexFormat
		spxDevice,
		&spxDstMesh);
	assert(SUCCEEDED(hr));


	// VertexArray in den ShadowMesh-VertexBuffer kopieren
	void* pMem;
	spxDstMesh->LockVertexBuffer(0, &pMem);
	memcpy(pMem, pDstVertices, sizeof(ShadowMeshVertex) * hndSrcMesh->GetNumFaces() * 3);
	spxDstMesh->UnlockVertexBuffer();

	delete [] pDstVertices;


	// IndexArray in den ShadowMesh-IndexBuffer kopieren
	spxDstMesh->LockIndexBuffer(0, &pMem);
	memcpy(pMem, pDstIndices, sizeof(unsigned short) * iDstIndexCount);
	spxDstMesh->UnlockIndexBuffer();

	delete [] pDstIndices;


	// fertig :)
	pxMeshContainer->DeleteAdjacencyArray();

	pxMeshContainer->SetMesh(ms_pxEngineController->GetMeshFactory()->RegisterMesh(spxDstMesh));

	pxMeshContainer->CreateAdjacencyArray();
	spxDstMesh->GenerateAdjacency(0, pxMeshContainer->Adjacency());
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CSceneGraphInitializer::ExtrudeShadowVolume(CD3DXMeshContainer* pxMeshContainer, CVec3 vVolumeExtrusion, float fShadowVolumeShrink)
{
	assert(!pxMeshContainer->UsesSkinning() && "cannot create static shadowmesh for skinned meshes");
	assert((pxMeshContainer->GetMesh()->GetOptions() & D3DXMESH_32BIT) == 0 && "32 bit index buffers are currently not supported!");


	CMat4S& matMeshContainerToWorld = pxMeshContainer->pFrameParent->matCombinedTransformation.GetInverse();
	vVolumeExtrusion = (matMeshContainerToWorld * vVolumeExtrusion.GetExtended(0)).GetReduced();


	TMeshHandle hndSrcMesh = pxMeshContainer->GetMesh();

	CComObjectPtr<IDirect3DDevice9> spxDevice;
	hndSrcMesh->GetDevice(&spxDevice);


	int iNumSrcVertices = pxMeshContainer->GetMesh()->GetNumVertices();
	int iNumSrcTriangles = pxMeshContainer->GetMesh()->GetNumFaces();
	int iSrcVertexSize = pxMeshContainer->GetMesh()->GetNumBytesPerVertex();
	int iSrcVertexPositionOffset = pxMeshContainer->CalcVertexElementOffset(D3DDECLUSAGE_POSITION);
	int iSrcVertexNormalOffset = pxMeshContainer->CalcVertexElementOffset(D3DDECLUSAGE_NORMAL);


	DWORD* pSrcPointReps = new DWORD[iNumSrcVertices];
	pxMeshContainer->GetMesh()->ConvertAdjacencyToPointReps(pxMeshContainer->Adjacency(), pSrcPointReps);


	int* pVertexRemap1 = new int[iNumSrcVertices];		// Remap-Daten für die Point-Representatives des Meshes (nicht extrudiert)
	int* pVertexRemap2 = new int[iNumSrcVertices];		// Remap-Daten für die Point-Representatives des Meshes (extrudiert)
	for (int iVertex = 0; iVertex < iNumSrcVertices; iVertex++)
	{
		pVertexRemap1[iVertex] = -1;
		pVertexRemap2[iVertex] = -1;
	}


	// Quell-MeshDaten
	unsigned char* pSrcVertices;
	unsigned short* pSrcIndices;
	DWORD* pSrcAdjacency;

	hndSrcMesh->LockVertexBuffer(0, (void**)&pSrcVertices);
	hndSrcMesh->LockIndexBuffer(0, (void**)&pSrcIndices);

	pSrcAdjacency = pxMeshContainer->Adjacency();



	// Ziel-MeshDaten
	struct ShadowMeshVertex
	{
		CVec3 vPos;
	};

	ShadowMeshVertex* pDstVertices = new ShadowMeshVertex[iNumSrcVertices * 2];
	unsigned short* pDstIndices = new unsigned short[iNumSrcTriangles * 3 * 4];

	int iNumDstVertices = 0;
	int iNumDstIndices = 0;


	// VertexWeld
	for (int iFace = 0; iFace < iNumSrcTriangles; iFace++)
	{
		const CVec3* pvCorner0 = (CVec3*)(pSrcVertices + (pSrcIndices[iFace * 3 + 0] * iSrcVertexSize + iSrcVertexPositionOffset));
		const CVec3* pvCorner1 = (CVec3*)(pSrcVertices + (pSrcIndices[iFace * 3 + 1] * iSrcVertexSize + iSrcVertexPositionOffset));
		const CVec3* pvCorner2 = (CVec3*)(pSrcVertices + (pSrcIndices[iFace * 3 + 2] * iSrcVertexSize + iSrcVertexPositionOffset));
		const CVec3 vFaceNormal = (*pvCorner2 - *pvCorner0) ^ (*pvCorner1 - *pvCorner0);

		const bool bExtrudeFace = (vFaceNormal * vVolumeExtrusion) < 0;


		for (int iCorner = 0; iCorner < 3; iCorner++)
		{
			int iSrcVertex = pSrcIndices[iFace * 3 + iCorner];									// VertexIndex des OriginalMeshes ermitteln
			int iVertexRepresentative = pSrcPointReps[iSrcVertex];									// Index des ersten Vorkommens der VertexPosition im VertexBuffer ermitteln

			CVec3* pvSourcePosition = (CVec3*)(pSrcVertices + (iVertexRepresentative * iSrcVertexSize + iSrcVertexPositionOffset));
			CVec3* pvSourceNormal = (iSrcVertexNormalOffset == -1) 
				? NULL
				: (CVec3*)(pSrcVertices + (iVertexRepresentative * iSrcVertexSize + iSrcVertexNormalOffset));

			if (iSrcVertexNormalOffset != -1 && 
				fShadowVolumeShrink != 0 &&
				*(CVec3*)(pSrcVertices + (iSrcVertex * iSrcVertexSize + iSrcVertexNormalOffset)) != *pvSourceNormal)
			{
				assert(0 && "ShadowVolumeShrink doesn't work with HardEdges in ShadowMesh!");
				MessageBox(NULL, "ShadowVolumeShrink doesn't work with HardEdges in ShadowMesh!", pxMeshContainer->GetName(), MB_ICONERROR);
				exit(-1);
			}

			int iCornerIdx;

			if (!bExtrudeFace)
			{
				if (pVertexRemap1[iVertexRepresentative] == -1)						// prüfen, ob der Vertex schon im neuen Buffer existiert
				{
					// Vertex am Ende einfügen
					pVertexRemap1[iVertexRepresentative] = iNumDstVertices;
					pDstVertices[iNumDstVertices].vPos = *pvSourcePosition;
					if (pvSourceNormal) pDstVertices[iNumDstVertices].vPos -= *pvSourceNormal * fShadowVolumeShrink;
					iNumDstVertices++;
				}

				iCornerIdx = pVertexRemap1[iSrcVertex] = pVertexRemap1[iVertexRepresentative];
			}
			else
			{
				if (pVertexRemap2[iVertexRepresentative] == -1)						// prüfen, ob der Vertex schon im neuen Buffer existiert
				{
					// Vertex am Ende einfügen
					pVertexRemap2[iVertexRepresentative] = iNumDstVertices;
					pDstVertices[iNumDstVertices].vPos = *pvSourcePosition + vVolumeExtrusion;
					if (pvSourceNormal) pDstVertices[iNumDstVertices].vPos -= *pvSourceNormal * fShadowVolumeShrink;
					iNumDstVertices++;
				}

				iCornerIdx = pVertexRemap2[iSrcVertex] = pVertexRemap2[iVertexRepresentative];
			}

			pDstIndices[iNumDstIndices++] = iCornerIdx;				// Index einfügen
		}
	}

	assert(iNumDstIndices == iNumSrcTriangles * 3);



	// Quads einfügen
	if (!vVolumeExtrusion.IsZero())
	{
		for (int iFace1 = 0; iFace1 < (int)hndSrcMesh->GetNumFaces(); iFace1++)
		{
			const CVec3* const apvCorner1[3] =
			{
				(CVec3*)(pSrcVertices + (pSrcIndices[iFace1 * 3 + 0] * iSrcVertexSize + iSrcVertexPositionOffset)),
				(CVec3*)(pSrcVertices + (pSrcIndices[iFace1 * 3 + 1] * iSrcVertexSize + iSrcVertexPositionOffset)),
				(CVec3*)(pSrcVertices + (pSrcIndices[iFace1 * 3 + 2] * iSrcVertexSize + iSrcVertexPositionOffset))
			};
			CVec3 vFace1Normal = (*apvCorner1[2] - *apvCorner1[0]) ^ (*apvCorner1[1] - *apvCorner1[0]);

			// Iteration über alle Edges dieses Dreiecks
			for (int iEdge1 = 0; iEdge1 < 3; iEdge1++)
			{
				//for (int iAdjacentFaceIdx = 0; iAdjacentFaceIdx < 3; iAdjacentFaceIdx++)
				int iAdjacentFaceIdx = iEdge1;	// funktioniert, ist aber nicht dokumentiert
				{
					// Index des angrenzenden Dreiecks ermitteln
					int iFace2 = pSrcAdjacency[iFace1 * 3 + iAdjacentFaceIdx];	// Reihenfolge der Faces blieb unverändert - daher kann SrcAdjacency verwendet werden
					if (iFace2 == -1)
					{
						MessageBox(NULL, "shadow geometry is not closed!", "Error", MB_OK);
						exit(-1);
					}
					assert(iFace2 != -1 && "geometry is not closed!");

					if (iFace2 != -1)
					{
						const CVec3* const apvCorner2[3] =
						{
							(CVec3*)(pSrcVertices + (pSrcIndices[iFace2 * 3 + 0] * iSrcVertexSize + iSrcVertexPositionOffset)),
							(CVec3*)(pSrcVertices + (pSrcIndices[iFace2 * 3 + 1] * iSrcVertexSize + iSrcVertexPositionOffset)),
							(CVec3*)(pSrcVertices + (pSrcIndices[iFace2 * 3 + 2] * iSrcVertexSize + iSrcVertexPositionOffset))
						};
						CVec3 vFace2Normal = (*apvCorner2[2] - *apvCorner2[0]) ^ (*apvCorner2[1] - *apvCorner2[0]);

						bool bFace1IsExtruded = (vFace1Normal * vVolumeExtrusion) < 0;
						bool bFace2IsExtruded = (vFace2Normal * vVolumeExtrusion) < 0;

						if (bFace1IsExtruded && !bFace2IsExtruded)
						{
							// Indices der Edge2 ermitteln 
							int iIndexStart1 =		pSrcIndices[iFace1 * 3 + (iEdge1 + 0) % 3];
							int iIndexEnd1 =		pSrcIndices[iFace1 * 3 + (iEdge1 + 1) % 3];

							#ifdef _DEBUG
							// Vertices der Edge1 ermitteln
							const CVec3* pvStart1 =	apvCorner1[(iEdge1 + 0) % 3];
							const CVec3* pvEnd1 =	apvCorner1[(iEdge1 + 1) % 3];
							#endif // _DEBUG


							bool bEdgeWasFound = false;

							// über die Edges des angrenzenden Dreiecks iterieren
							for (int iEdge2 = 0; iEdge2 < 3; iEdge2++)
							{
								if (pSrcAdjacency[iFace2 * 3 + iEdge2] == iFace1)
								{
									// Indices der Edge2 ermitteln 
									int iIndexStart2 =		pSrcIndices[iFace2 * 3 + (iEdge2 + 0) % 3];
									int iIndexEnd2 =		pSrcIndices[iFace2 * 3 + (iEdge2 + 1) % 3];

									#ifdef _DEBUG
									// Vertices der Edge2 ermitteln
									const CVec3* pvStart2 =	apvCorner2[(iEdge2 + 0) % 3];
									const CVec3* pvEnd2 =	apvCorner2[(iEdge2 + 1) % 3];
									#endif // _DEBUG

									assert(*pvStart1 == *pvEnd2 && *pvEnd1 == *pvStart2);

									bEdgeWasFound = true;

									assert( pVertexRemap1[iIndexEnd2] != -1 && pVertexRemap2[iIndexEnd1] != -1 &&
											pVertexRemap1[iIndexStart2] != -1 && pVertexRemap2[iIndexStart1] != -1);

									// Indices für das Quad hinzufügen
									pDstIndices[iNumDstIndices + 0] = pVertexRemap2[iIndexEnd1];
									pDstIndices[iNumDstIndices + 1] = pVertexRemap2[iIndexStart1];
									pDstIndices[iNumDstIndices + 2] = pVertexRemap1[iIndexStart2];

									pDstIndices[iNumDstIndices + 3] = pVertexRemap1[iIndexStart2];
									pDstIndices[iNumDstIndices + 4] = pVertexRemap2[iIndexStart1];
									pDstIndices[iNumDstIndices + 5] = pVertexRemap1[iIndexEnd2];

									iNumDstIndices += 6;

									break;
								}
							}

							assert(bEdgeWasFound); // gesuchte Edge wurde im Nachbardreieck nicht gefunden - evtl. ist die Annahme über die Reihenfolge der Adjacency-Daten nicht korrekt
						}
					}
				}
			}
		}
	}

	hndSrcMesh->UnlockVertexBuffer();
	hndSrcMesh->UnlockIndexBuffer();

	delete [] pVertexRemap2;
	delete [] pVertexRemap1;
	delete [] pSrcPointReps;



	CComObjectPtr<ID3DXMesh> spxShadowMesh;
	D3DXCreateMeshFVF(iNumDstIndices / 3, iNumDstVertices, D3DXMESH_DYNAMIC | D3DXMESH_SYSTEMMEM, D3DFVF_XYZ, spxDevice, &spxShadowMesh);


	void* pIndexBufferMemory;
	spxShadowMesh->LockIndexBuffer(0, &pIndexBufferMemory);
	memcpy(pIndexBufferMemory, pDstIndices, sizeof(unsigned short) * iNumDstIndices);
	spxShadowMesh->UnlockIndexBuffer();

	delete [] pDstIndices;


	void* pVertexBufferMemory;
	spxShadowMesh->LockVertexBuffer(0, &pVertexBufferMemory);
	memcpy(pVertexBufferMemory, pDstVertices, sizeof(ShadowMeshVertex) * iNumDstVertices);
	spxShadowMesh->UnlockVertexBuffer();

	delete [] pDstVertices;

	pxMeshContainer->DeleteAdjacencyArray();
	pxMeshContainer->SetMesh(ms_pxEngineController->GetMeshFactory()->RegisterMesh(spxShadowMesh));
	pxMeshContainer->CreateAdjacencyArray();
	spxShadowMesh->GenerateAdjacency(0, pxMeshContainer->Adjacency());
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CSceneGraphInitializer::InitializeMeshContainer(CD3DXMeshContainer* pxMeshContainer)
{
	if (ms_pxMeshLoaderOptions->m_bLoadResources)
	{
		CreateMaterials(pxMeshContainer);
		SetupShaderTextures(pxMeshContainer);
	}


	CleanMesh(pxMeshContainer);


	if (ms_pxMeshLoaderOptions->m_fLevelOfDetailFactor != 1)
	{
		SimplifyMesh(pxMeshContainer, ms_pxMeshLoaderOptions->m_fLevelOfDetailFactor);
	}


	if (ms_pxMeshLoaderOptions->m_bCreateEdgeQuads)
	{
		CreateEdgeQuads(pxMeshContainer, ms_pxMeshLoaderOptions->m_fShadowVolumeShrink);
	}


	if (!ms_pxMeshLoaderOptions->m_vStaticShadowVolumeExtrusion.IsZero())
	{
		assert(ms_pxMeshLoaderOptions->m_bCreateEdgeQuads == false);
		ExtrudeShadowVolume(pxMeshContainer, ms_pxMeshLoaderOptions->m_vStaticShadowVolumeExtrusion, ms_pxMeshLoaderOptions->m_fShadowVolumeShrink);
	}


	if (pxMeshContainer->UsesSkinning())
	{
		SetupSkinningData(pxMeshContainer);
	}


	pxMeshContainer->SetupBoneMatrixPointers(ms_pxRootFrame);


	SetupVertexStreams(pxMeshContainer);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CSceneGraphInitializer::InitializeFrameHierarchy(CD3DXFrame* pxFrameRoot, CEngineController* pxEngineController, CMeshLoaderOptions* pxOptions)
{
	ms_pxRootFrame = pxFrameRoot;
	ms_pxEngineController = pxEngineController;
	ms_pxMeshLoaderOptions = pxOptions;


	// parent Pointer setzen (muss gemacht werden bevor man iterieren kann)
	RecursiveSetupParentPointers(pxFrameRoot);


	// Visibilityswitch-Frame setzen (für animierbare Visibility)
	{
		CSceneGraphIterator xSGI(pxFrameRoot);
		CD3DXFrame* pxFrame;
		while (pxFrame = xSGI.GetNextFrame())
		{
			InitVisibilitySwitchFrame(pxFrame);
		}
	}


	// kombinierte Transformationsmatrizen initialisieren
	CSceneGraphRenderer::UpdateCombinedFrameMatrices(pxFrameRoot);


	// MeshContainer initialisieren
	{
		CSceneGraphIterator xSGI(pxFrameRoot);
		CD3DXMeshContainer* pxMeshContainer;
		while (pxMeshContainer = xSGI.GetNextMeshContainer())
		{
			InitializeMeshContainer(pxMeshContainer);
		}
	}


	// BoundingSpheren berechnen
	{
		CSceneGraphIterator xSGI(pxFrameRoot);
		CD3DXFrame* pxFrame;
		while (pxFrame = xSGI.GetNextFrame())
		{
			CalcBoundingSphere(pxFrame);
		}
	}


	ms_pxMeshLoaderOptions = NULL;
	ms_pxEngineController = NULL;
	ms_pxRootFrame = NULL;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
