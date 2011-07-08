//-----------------------------------------------------------------------------------------------------------------------------------------
inline
const char*
CD3DXMeshContainer::GetName() const
{
	return this->Name;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CD3DXMeshContainer*& 
CD3DXMeshContainer::NextMeshContainer()
{
	return (CD3DXMeshContainer*&)this->pNextMeshContainer;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CD3DXMeshContainer* const& 
CD3DXMeshContainer::NextMeshContainer() const
{
	return (CD3DXMeshContainer* const&)this->pNextMeshContainer;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void
CD3DXMeshContainer::SetMesh(const TMeshHandle& hndMesh)
{
	if (this->pAdjacency)
	{
		// falls sich die Anzahl der Faces geändert hat, darf keine Adjacency-Data existieren
		assert((hndMesh.GetPtr() == NULL && this->hndMesh.GetPtr() == NULL) ||
			(hndMesh.GetPtr() != NULL && this->hndMesh.GetPtr() != NULL && hndMesh->GetNumFaces() == this->hndMesh.GetPtr()->GetNumFaces()));
	}

	this->hndMesh = hndMesh;
	this->MeshData.pMesh = hndMesh.GetPtr();    // SuperklassenMember updaten (für upcast)
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
const TMeshHandle&
CD3DXMeshContainer::GetMesh() const
{
	assert(this->MeshData.pMesh == this->hndMesh.GetPtr());
	return hndMesh;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CComObjectPtr<ID3DXSkinInfo>&
CD3DXMeshContainer::SkinInfo() 
{
	return *(CComObjectPtr<ID3DXSkinInfo>*)&(this->pSkinInfo);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
const CComObjectPtr<ID3DXSkinInfo>&
CD3DXMeshContainer::SkinInfo() const
{
	return *(const CComObjectPtr<ID3DXSkinInfo>*)&(this->pSkinInfo);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
bool
CD3DXMeshContainer::UsesSkinning() const
{
	return this->pSkinInfo != NULL;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
DWORD
CD3DXMeshContainer::GetNumAttributeGroups() const
{
	if (this->pSkinInfo)
	{
		return this->dwNumBoneCombinations;
	}
	else
	{
		return __super::NumMaterials;
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
DWORD*&
CD3DXMeshContainer::Adjacency()
{
	return this->pAdjacency;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
DWORD* const&
CD3DXMeshContainer::Adjacency() const
{
	return this->pAdjacency;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void 
CD3DXMeshContainer::CreateAdjacencyArray()
{
	assert(this->pAdjacency == NULL);
	assert(hndMesh.GetPtr());
	this->pAdjacency = new DWORD[hndMesh.GetPtr()->GetNumFaces() * 3];
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void 
CD3DXMeshContainer::DeleteAdjacencyArray()
{
	delete [] this->pAdjacency;
	this->pAdjacency = NULL;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CMaterial*
CD3DXMeshContainer::GetMaterials() const
{
	return this->pxMaterials;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CD3DXMaterial*
CD3DXMeshContainer::GetD3DXMaterials() const
{
	return (CD3DXMaterial*)this->pMaterials;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CD3DXEffectInstance*
CD3DXMeshContainer::GetD3DXEffectInstances() const
{
	return (CD3DXEffectInstance*)this->pEffects;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
DWORD
CD3DXMeshContainer::GetNumMaterials() const
{
	return __super::NumMaterials;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void 
CD3DXMeshContainer::SetMeshType(D3DXMESHDATATYPE type)
{
	this->MeshData.Type = type;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
D3DXMESHCONTAINER&
CD3DXMeshContainer::D3DXMeshContainer()
{
	assert(this->MeshData.pMesh == this->hndMesh.GetPtr());
	return *this;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
const D3DXMESHCONTAINER&
CD3DXMeshContainer::D3DXMeshContainer() const
{
	assert(this->MeshData.pMesh == this->hndMesh.GetPtr());
	return *this;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
