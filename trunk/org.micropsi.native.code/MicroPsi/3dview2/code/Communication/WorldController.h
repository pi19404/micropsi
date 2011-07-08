#ifndef WORLDCONTROLLER_H_INCLUDED
#define WORLDCONTROLLER_H_INCLUDED

#include <string>
#include "baselib/geometry/CVector.h"

class CWorld;

class CWorldController
{
public:

	CWorldController(CWorld* p_pxWorld) : m_pxWorld(p_pxWorld) {};
	virtual ~CWorldController() {};

	virtual void		ClearAllObjects() = 0;
	virtual	bool		SaveWorld() = 0; 
	virtual	bool		SaveWorldAs(const std::string& p_rsFilename) = 0; 
	virtual std::string	GetCurrentWorldFileName() const = 0;

	/// create a new object; set variation to -1 to get a random variation
	virtual void		CreateNewObject(const char* p_pcObjectClassName, const CVec3& p_vPos, 
										float p_fOrientationAngle, float p_fHeight, int p_iVariation = -1) = 0;

	virtual bool		DeleteObject(__int64 p_iID) = 0;

	virtual void		Tick(double p_dTime) {};

protected:

	CWorld*				m_pxWorld;			///< the world we control

private:

};

#endif // WORLDCONTROLLER_H_INCLUDED
