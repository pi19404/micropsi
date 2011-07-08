#ifndef OFFLINEWORLDCONTROLLER_H_INCLUDED
#define OFFLINEWORLDCONTROLLER_H_INCLUDED

#include "Communication/WorldController.h"

class COfflineWorldController : public CWorldController
{
public:

	COfflineWorldController(CWorld* p_pxWorld);
	virtual ~COfflineWorldController();    

	virtual void		ClearAllObjects();
	virtual	bool		SaveWorld(); 
	virtual	bool		SaveWorldAs(const std::string& p_rsFilename); 
	virtual std::string	GetCurrentWorldFileName() const;

	virtual void		CreateNewObject(const char* p_pcObjectClassName, const CVec3& p_vPos, 
										float p_fOrientationAngle, float p_fHeight, int p_iVariation = -1);

	virtual bool		DeleteObject(__int64 p_iID);

	virtual void		Tick(double p_dTime);

protected:


private:

};

#endif // OFFLINEWORLDCONTROLLER_H_INCLUDED
