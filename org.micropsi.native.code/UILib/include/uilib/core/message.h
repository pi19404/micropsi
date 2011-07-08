#ifndef UILIB_MESSAGE_H_INCLUDED
#define UILIB_MESSAGE_H_INCLUDED

#include <string.h>
#include "baselib/macros.h"
#include "windowhandle.h"
#include "baselib/pnt.h"

namespace UILib
{

/**
	Fenster schicken sich gegenseitig Nachrichten bzw. bekommen welche von außen (Maus, Tastatur, Größenänderung...)
*/

class CMessage 
{
protected: 
		
	union
	{
		char		m_acMessageTypeName[8];				///< Typ diese Message
		__int64		m_iMessageType;						///< das gleiche, leichter in der Zuweisung, im Vergleich
	};
	__int64			m_iTime;							///< Uhrzeit, zu der diese Message erzeugt wurde

	struct 
	{
		unsigned int m_bMouseMessage : 1;				///< true, falls das eine Mouse-Message ist
		unsigned int m_bBubblesUp : 1;					///< true, falls diese Message "hochbubbled", d.h. an Elternfenster weitergeht
	};

public: 

	CMessage(const char* p_pcID, bool p_bMouse, bool p_bBubbleUp, int p_iX = 0, int p_iY = 0, char p_cKeyMod = 0)	
	{
		strncpy(m_acMessageTypeName, p_pcID, 8);
		m_bMouseMessage = p_bMouse;
		m_bBubblesUp = p_bBubbleUp;
		m_short[0] = (short) p_iX;
		m_short[1] = (short) p_iY;
		m_char[4] = p_cKeyMod;
	}


	CMessage(const char* p_pcID, bool p_bMouse, bool p_bBubbleUp, int p_iKey, char p_cKeyMod)	
	{
		strncpy(m_acMessageTypeName, p_pcID, 8);
		m_bMouseMessage = p_bMouse;
		m_bBubblesUp = p_bBubbleUp;
		m_long[0] = (long) p_iKey;
		m_char[4] = p_cKeyMod;
	}


	CMessage(const char* p_pcID, bool p_bMouse, bool p_bBubbleUp, unsigned long p_iTimerID)	
	{
		strncpy(m_acMessageTypeName, p_pcID, 8);
		m_bMouseMessage = p_bMouse;
		m_bBubblesUp = p_bBubbleUp;
		m_long[0] = p_iTimerID;
	}


	CMessage(const char* p_pcID, bool p_bMouse, bool p_bBubbleUp, WHDL p_hWnd)	
	{
		strncpy(m_acMessageTypeName, p_pcID, 8);
		m_bMouseMessage = p_bMouse;
		m_bBubblesUp = p_bBubbleUp;
		m_long[1] = p_hWnd;
	}

	union												
	{
		char		m_char[8];
		short		m_short[4];
		long		m_long[2];
		float		m_float[2];
		double		m_double;
	};

	int		GetX() const			{ return (int) m_short[0]; }
	int		GetY() const			{ return (int) m_short[1]; }
	CPnt	GetPos() const			{ return CPnt(GetX(), GetY()); }

	int		GetKey() const			{ return (int) m_long[0]; } 

	int		GetIntParameter() const	{ return (int) m_long[0]; } 

	int		GetTimerID() const		{ return m_long[0]; }
	WHDL	GetWindow()	const		{ return WHDL(m_long[1]); }

	bool	IsMouseMessage() const	{ return m_bMouseMessage; }
	bool	DoesBubbleUp() const	{ return m_bBubblesUp; }
	char	GetKeyModifier() const	{ return m_char[4]; }

	/// Vergleichsoperator; vergleicht Messagetyp
	bool	operator==(const char* p_pcMessageType) const	
	{
		return strncmp(p_pcMessageType, m_acMessageTypeName, 8) == 0;
	}

	/// Vergleichsoperator; vergleicht Messagetyp
	bool	operator!=(const char* p_pcMessageType) const	
	{
		return strncmp(p_pcMessageType, m_acMessageTypeName, 8) != 0;
	}

};


//----- Fenster - Nachrichten -------------------------------------------------

static const char* msgWindowActivation= "WndAct";
class CWindowActivationMsg : public CMessage
{ public:	CWindowActivationMsg() : CMessage(msgWindowActivation, false, false) {} }; 


static const char* msgWindowDeactivation= "WndDeact";
class CWindowDeactivationMsg : public CMessage
{ public:	CWindowDeactivationMsg() : CMessage(msgWindowDeactivation, false, false) {} }; 


static const char* msgWindowIndirectActivation= "WndActI";
class CWindowIndirectActivationMsg : public CMessage
{ public:	CWindowIndirectActivationMsg() : CMessage(msgWindowIndirectActivation, false, false) {} }; 


static const char* msgWindowIndirectDeactivation= "WndDactI";
class CWindowIndirectDeactivationMsg : public CMessage
{ public:	CWindowIndirectDeactivationMsg() : CMessage(msgWindowIndirectDeactivation, false, false) {} }; 



static const char* msgWindowSizeChange= "sWSize";
class CWindowSizeChangeMsg : public CMessage
{ public:	CWindowSizeChangeMsg() : CMessage(msgWindowSizeChange, false, false) {} }; 


static const char* msgWindowChildResized= "sChWSize";
class CWindowChildResizedMsg : public CMessage
{ public:	CWindowChildResizedMsg(WHDL p_hWnd) : CMessage(msgWindowChildResized, false, false, p_hWnd) {} }; 


static const char* msgWindowDeviceChange= "sWDevChg";
class CWindowDeviceChangeMsg : public CMessage
{ public:	CWindowDeviceChangeMsg() : CMessage(msgWindowDeviceChange, false, false) {} }; 


static const char* msgWindowVisualizationChange= "sWVisChg";
class CWindowVisualizationChangeMsg : public CMessage
{ public:	CWindowVisualizationChangeMsg() : CMessage(msgWindowVisualizationChange, false, false) {} }; 


// ----- Maus - Nachrichten ---------------------------------------------------

static const char* msgMouseLeftButtonDown = "MLBDwn";
class CMouseLeftButtonDownMsg : public CMessage
{ public: CMouseLeftButtonDownMsg(int x, int y, char keymod) : CMessage(msgMouseLeftButtonDown, true, true, x, y, keymod)	{} };

static const char* msgMouseRightButtonDown = "MRBDwn";
class CMouseRightButtonDownMsg : public CMessage
{ public: CMouseRightButtonDownMsg(int x, int y, char keymod) : CMessage(msgMouseRightButtonDown, true, true, x, y, keymod)	{} };

static const char* msgMouseMiddleButtonDown = "MMBDwn";
class CMouseMiddleButtonDownMsg : public CMessage
{ public: CMouseMiddleButtonDownMsg(int x, int y, char keymod) : CMessage(msgMouseMiddleButtonDown, true, true, x, y, keymod)	{} };

static const char* msgMouseLeftButtonUp = "MLBUp";
class CMouseLeftButtonUpMsg : public CMessage
{ public: CMouseLeftButtonUpMsg(int x, int y, char keymod) : CMessage(msgMouseLeftButtonUp, true, true, x, y, keymod)	{} };

static const char* msgMouseRightButtonUp = "MRBUp";
class CMouseRightButtonUpMsg : public CMessage
{ public: CMouseRightButtonUpMsg(int x, int y, char keymod) : CMessage(msgMouseRightButtonUp, true, true, x, y, keymod)	{} };

static const char* msgMouseMiddleButtonUp = "MMBUp";
class CMouseMiddleButtonUpMsg : public CMessage
{ public: CMouseMiddleButtonUpMsg(int x, int y, char keymod) : CMessage(msgMouseMiddleButtonUp, true, true, x, y, keymod)	{} };

static const char* msgMouseLeftButtonDoubleClick = "MLBDbl";
class CMouseLeftButtonDoubleClickMsg : public CMessage
{ public: CMouseLeftButtonDoubleClickMsg(int x, int y, char keymod) : CMessage(msgMouseLeftButtonDoubleClick, true, true, x, y, keymod)	{} };

static const char* msgMouseRightButtonDoubleClick = "MRBDbl";
class CMouseRightButtonDoubleClickMsg : public CMessage
{ public: CMouseRightButtonDoubleClickMsg(int x, int y, char keymod) : CMessage(msgMouseRightButtonDoubleClick, true, true, x, y, keymod)	{} };

static const char* msgMouseMiddleButtonDoubleClick = "MMBDbl";
class CMouseMiddleButtonDoubleClickMsg : public CMessage
{ public: CMouseMiddleButtonDoubleClickMsg(int x, int y, char keymod) : CMessage(msgMouseMiddleButtonDoubleClick, true, true, x, y, keymod)	{} };

static const char* msgMouseMove = "MMove";
class CMouseMoveMsg : public CMessage
{ public: CMouseMoveMsg(int x, int y, char keymod) : CMessage(msgMouseMove, true, true, x, y, keymod)	{} };


// ----- Maus - Enter und Leave - Nachrichten ---------------------------------

static const char* msgMouseEnter = "MEnter";
class CMouseEnterMsg : public CMessage
{ public: CMouseEnterMsg() : CMessage(msgMouseEnter, false, false)	{} };

static const char* msgMouseLeave = "MLeave";
class CMouseLeaveMsg : public CMessage
{ public: CMouseLeaveMsg() : CMessage(msgMouseLeave, false, false)	{} };


// ----- Tastatur - Nachrichten -----------------------------------------------

static const char* msgCharacterKey = "sCharKey";
class CCharacterKeyMsg : public CMessage
{ public: CCharacterKeyMsg(int key, char keymod) : CMessage(msgCharacterKey, false, true, key, keymod)	{} };

static const char* msgControlKey = "sCtrlKey";
class CControlKeyMsg : public CMessage
{ public: CControlKeyMsg(int key, char keymod) : CMessage(msgControlKey, false, true, key, keymod)	{} };

static const char* msgKeyDown = "sKeyDown";
class CKeyDownMsg : public CMessage
{ public: CKeyDownMsg(int key, char keymod) : CMessage(msgKeyDown, false, true, key, keymod)	{} };

static const char* msgKeyUp = "sKeyUp";
class CKeyUpMsg : public CMessage
{ public: CKeyUpMsg(int key, char keymod) : CMessage(msgKeyUp, false, true, key, keymod)	{} };


// ----- Timer - Nachrichten --------------------------------------------------

static const char* msgTimer = "Timer";
class CTimerMsg : public CMessage
{ public: CTimerMsg(unsigned long timerid) : CMessage(msgTimer, false, true, timerid)	{} };


} // namespace UILib


#endif // ifndef UILIB_MESSAGE_H_INCLUDED

