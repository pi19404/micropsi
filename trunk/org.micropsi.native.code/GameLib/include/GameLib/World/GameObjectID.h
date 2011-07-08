#ifndef GAMELIB_GAMEOBJECTID_H_INCLUDED
#define GAMELIB_GAMEOBJECTID_H_INCLUDED

#include "baselib/HandledSet.h"

// die Klasse wurde nur geschrieben, damit sich das enum auf INVALID initialisiert
class GameObjID
{
public:
	enum
	{
		INVALID_GAMEOBJ_ID		= -1
	};
	
	int m_iID;

    GameObjID() :                       m_iID(-1)   {}
    GameObjID(int i) :                  m_iID(i)    {}
    ~GameObjID()                        {}
    operator int()                      { return m_iID;}
    GameObjID& operator=(int i)         { m_iID = i; return *this; }
    GameObjID& operator=(GameObjID id)  { m_iID = id.m_iID; return *this; }
    bool operator==(int i)              { return m_iID == i; }
    bool operator==(GameObjID id)       { return m_iID == id.m_iID; }
    bool operator!=(int i)              { return m_iID != i; }
    bool operator!=(GameObjID id)       { return m_iID != id.m_iID; }
};

#endif // GAMEOBJECTID_H_INCLUDED
