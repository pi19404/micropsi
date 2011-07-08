#ifndef CFACE_H_INCLUDED
#define CFACE_H_INCLUDED

#include "stdinc.h"
#include "e42/core/ResourceHandles.h"
#include "e42/core/RenderContext.h"

class CAnimationCtrl;

class CFace
{
public:
	CFace(std::string p_sModelName);
	virtual ~CFace();

	class CBone
	{
	public:
		std::string			m_sName;
		CVec3				m_vStartPos;
		CQuat				m_qStartRotation;
		CVec3				m_vEndPos;
		CQuat				m_qEndRotation;
	};

	void	Render(TRenderContextPtr spxRenderContext);

	bool	AddBone(std::string p_sName, bool p_bOnlyIfKeyed = true);

	void	ResetAllBones(float fWeight = 0.5f);

	/// interpolates bone between its start and end position; weight must be between 0.0 (= start pos) and 1.0 (= end pos)
	bool	SetBonePos(std::string p_sName, float p_fInterpolationFactor);

	/// get all bones (read-only)
	const std::map<std::string, CBone>& GetBones() const;

private:
	
	CMat4S	CalcTransform() const;

    CMat3S              m_mRot;							///< rotation matrix
    CVec3               m_vPos;							///< world position

    TModelHandle        m_hModel;						///< handle to model resource
	CAnimationCtrl*     m_pAnimationCtrl;				///< animation controller

	std::map< std::string, CBone >		m_axAllBones;
};

#include "Face.inl"

#endif // #ifndef CFACE_H_INCLUDED

