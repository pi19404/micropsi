#include "face.h"

#include "e42/core/ModelFactory.h"
#include "e42/core/RenderContext.h"
#include "e42/core/D3DXFrame.h"
#include "e42/E42Application.h"
#include "e42/AnimationCtrl.h"

using std::map;
using std::string;

//---------------------------------------------------------------------------------------------------------------------
CFace::CFace(std::string p_sModelName)
{
    m_vPos = CVec3(0, 0, 0);
    m_mRot.SetIdentity();

	m_hModel = CE42Application::Get().GetModelFactory()->CreateModelFromFile(std::string("model>") + p_sModelName);

	assert(m_hModel->HasAnimations());
	if(m_hModel->HasAnimations())
	{
	    m_pAnimationCtrl = new CAnimationCtrl(&CE42Application::Get());
		m_pAnimationCtrl->SetModel(m_hModel);
//		m_pAnimationCtrl->StartAnimation(".default");
	}

	AddBone("Bone_Hals_oben_Rotation");
	AddBone("Bone_Hals_oben_Neigung");
	AddBone("Bone_Hals_Oben");
	AddBone("Bone_Kopf_Rotation");
	AddBone("Bone_Kopf_Neigung");
	AddBone("Bone_Kopf");
	AddBone("Bone_Kiefer_Zwischengelenk");
	AddBone("Zahnfleisch_oben");
	AddBone("Z_hne_oben");
	AddBone("Bone_Unterkiefer");
	AddBone("Zahnfleisch_unten");
	AddBone("Z_hne_unten");
	AddBone("Bone_Zunge");
	AddBone("Zunge01");
	AddBone("L_Bone_Nasenfl_gel");
	AddBone("R_Bone_Nadenfl_gel_");
	AddBone("R_Bone_Mund_Breite");
	AddBone("R_Bone_Mundwinkel");
	AddBone("R_Bone_Mund_oben");
	AddBone("L_Bone_Mund_oben");
	AddBone("L_Bone_Mund_Breite");
	AddBone("L_Bone_Mundwinkel");
	AddBone("Bone_Oberlippe");
	AddBone("Bone_Unterlippe");
	AddBone("R_Bone_Wange");
	AddBone("L_Bone_Wange");
	AddBone("R_Bone_Braue_aussen_");
	AddBone("R_Bone_Braue_mitte");
	AddBone("R_Bone_Braue_innen");
	AddBone("L_Bone_Braue_innen");
	AddBone("L_Bone_Braue_mitte");
	AddBone("L_Bone_Braue_aussen");
	AddBone("R_Bone_Auge_horizontal");
	AddBone("R_Bone_Auge_vertikal");
	AddBone("AugapfelR");
	AddBone("L_Bone_Auge_horizontal");
	AddBone("L_Bone_Auge_vertikal");
	AddBone("AugapfelL");
	AddBone("L_Bone_Augenlid_unten");
	AddBone("L_Bone_Augenlid_oben");
	AddBone("R_Bone_Augenlid_oben");
	AddBone("R_Bone_Augenlid_unten_");
	ResetAllBones();
}
//---------------------------------------------------------------------------------------------------------------------
CFace::~CFace()
{
    delete m_pAnimationCtrl;
    m_pAnimationCtrl = 0;
}
//---------------------------------------------------------------------------------------------------------------------
void 
CFace::Render(TRenderContextPtr spxRenderContext)
{
    if (m_hModel)
    {
        if (m_pAnimationCtrl)
        {
            m_pAnimationCtrl->SetupSound(m_vPos);
            m_pAnimationCtrl->SetupModel();
        }

        TRenderContextPtr spxNewRenderContext;
        spxNewRenderContext.Create();
        *spxNewRenderContext = *spxRenderContext;

        m_hModel->Render(spxNewRenderContext, CalcTransform());
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CMat4S
CFace::CalcTransform() const
{
    return CMat4S(
        m_mRot(0, 0),   m_mRot(0, 1),   m_mRot(0, 2),   0,
        m_mRot(1, 0),   m_mRot(1, 1),   m_mRot(1, 2),   0,
        m_mRot(2, 0),   m_mRot(2, 1),   m_mRot(2, 2),   0,
        m_vPos.x(),     m_vPos.y(),     m_vPos.z(),     1);
}
//---------------------------------------------------------------------------------------------------------------------
bool	
CFace::AddBone(std::string p_sName, bool p_bOnlyIfKeyed)
{
	if(p_bOnlyIfKeyed)
	{
		if(!m_hModel->FrameIsKeyedInAnimation(".default", p_sName.c_str()))
		{
			return true;
		}
	}

	CBone xBone;
	xBone.m_sName = p_sName;


	m_hModel->SetAnimation(".default", 0.1f);
	CMat4S mBoneTransformStart;
	
	CD3DXFrame* pBoneStart = m_hModel->GetFrameByName(p_sName.c_str());
    assert(pBoneStart);
	if(!pBoneStart)
	{
		assert(false);
		return false;
	}
    mBoneTransformStart = pBoneStart->TransformationMatrix();

	m_hModel->SetAnimation(".default", m_hModel->GetAnimationLength(".default") - 0.001f);
	CMat4S mBoneTransformEnd;


    CD3DXFrame* pBoneEnd = m_hModel->GetFrameByName(p_sName.c_str());
    assert(pBoneEnd);
	if(!pBoneEnd)
	{
		assert(false);
		return false;
	}
    mBoneTransformEnd = pBoneEnd->TransformationMatrix();

	xBone.m_vStartPos = mBoneTransformStart.GetTranslation();
	xBone.m_qStartRotation = mBoneTransformStart.ToQuaternion();

	xBone.m_vEndPos = mBoneTransformEnd.GetTranslation();
	xBone.m_qEndRotation = mBoneTransformEnd.ToQuaternion();

	m_axAllBones[p_sName] = xBone;

	return true;
}


//---------------------------------------------------------------------------------------------------------------------
void
CFace::ResetAllBones(float fWeight)
{
//	m_hModel->SetAnimation(".default", 0.1f);

	map<string, CBone>::iterator i;
	for(i=m_axAllBones.begin(); i!=m_axAllBones.end(); i++)
	{
		SetBonePos(i->second.m_sName, fWeight);
	}
}

//---------------------------------------------------------------------------------------------------------------------
/**
	interpolates bone between its start and end position; weight must be between 0.0 (= start pos) and 1.0 (= end pos)
*/
bool	
CFace::SetBonePos(std::string p_sName, float p_fInterpolationFactor)
{
	if(m_axAllBones.empty()) 
	{ 
		assert(false);
		return false; 
	}

	map<string, CBone>::iterator i;
	i = m_axAllBones.find(p_sName);
	if(i == m_axAllBones.end()) 
	{
		assert(false);
		return false ;
	}

	CQuat q;
	q.Slerp(i->second.m_qStartRotation, i->second.m_qEndRotation, p_fInterpolationFactor);

	CMat4S mInterpolatedMatrix;
	mInterpolatedMatrix.FromQuaternion(q);
	mInterpolatedMatrix.Translate(CVec3::GetLerp(i->second.m_vStartPos, i->second.m_vEndPos, 0.0f));

    CD3DXFrame* pBone = m_hModel->GetFrameByName(p_sName.c_str());
    assert(pBone);
    pBone->TransformationMatrix() = mInterpolatedMatrix;
    m_hModel->InvalidateCombinedFrameMatrizes();

	return true;
}
//---------------------------------------------------------------------------------------------------------------------

