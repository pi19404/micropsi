#ifndef UILIB_LISTBOX_H_INCLUDED 
#define UILIB_LISTBOX_H_INCLUDED

#include "uilib/core/window.h"
#include "uilib/controls/scrollbar.h"
#include "uilib/core/itemdata.h"


namespace UILib
{

class CListBox : public CWindow
{
public:
	static CListBox*	Create();

	virtual int			NumItems() const;
	virtual bool		IsEmpty() const;
	virtual void		Clear();
	virtual int			AddItem(const CStr& p_sString);
	virtual int			AddItem(const CStr& p_sString, const CBitmap* p_pxBitmap);
	virtual int			AddItem(const CStr& p_sString, CItemData* p_pxItemData);
	virtual int			AddItem(const CStr& p_sString, CItemData* p_pxItemData, const CBitmap* p_pxBitmap);
	virtual CStr		GetItem(int p_iIndex) const;
	virtual int 		FindItem(const CStr& p_sString) const;
	virtual int			DeleteItem(int p_iIndex);
	virtual void		SetItemText(int p_iIndex, const CStr& p_rsString);

	void*				GetItemDataEx(int p_iIndex) const;
	void				SetItemDataEx(int p_iIndex,void* p_pItemData);

	void				SetItemData(int p_iIndex,CItemData* p_pxItemData);
	CItemData*			GetItemData(int p_iIndex) const;

	virtual int			GetSelectedItem() const;
	virtual CStr		GetSelectedItemAsString() const;
	int					GetNumberOfSelectedItems() const;
	bool				IsItemSelected(int p_iIndex) const;
	void				StartIterateSelectedItems(int& po_iIterator) const;
	bool 				IterateSelectedItems(int& po_iIterator) const;

	/// select object with index; deselects everything else (if multiselect is allowed)
	virtual int			Select(int p_iIndex);

	/// select object with index; you may chose to deselect everything else or keep previous selection (if multiselect is allowed)
	virtual int			Select(int p_iIndex, bool p_bUnselectRest);

	  virtual void		Select(int p_iFrom, int p_iTo);

	/// unselect an item
	virtual int			UnSelect(int p_iIndex);

	/// remove entire selection
	virtual void		RemoveSelection();
	virtual void		RemoveSelectionNoMsg();

	/// scroll list so selection is visible
	virtual void		ShowSelection();

	virtual bool		SelectNextItemBeginningWithChar(int p_iChar);
	virtual bool		SelectItemBeginningWithString(const CStr& p_sString);

	/// allow / disallow scrollbar
	virtual void		SetAllowScrollBar(bool p_bAllowScrollbar = true);

	/// liefert true wenn Scrollbars erlaubt sind
	bool				GetAllowScrollBar() const;

	/// allow / disallow multiselection
	virtual void		SetAllowMultiSelection(bool p_bAllowMultiSelection = true);

	/// \return true if multiselection is allowed
	virtual bool		GetAllowMultiSelection() const;

	/// invoked when the control Selects (i.e. selection Select)
	virtual bool		OnSelect(); 

	/// setzt Callbackfunktion für "Select"
	void				SetOnSelectCallback(CFunctionPointer1<CListBox*>& rxCallback);

	CScrollBar*			GetCurrentScrollBar() const;

	virtual bool		SetAttrib(const CStr& p_rsName, const CStr& p_rsValue);
	virtual bool		GetAttrib(const CStr& p_rsName, CStr& po_srValue) const;

	/// \return height (in pixels) ctrl needs for given number of lines
	int					GetNeededHeight(int p_iLines);

protected:
	
	CListBox();
	virtual ~CListBox();

	virtual bool		HandleMsg(const CMessage& p_rxMessage);
	virtual void		Paint(const CPaintContext& p_rxCtx);

	virtual bool		OnLButtonDown(const CPnt& p_rxMousePos, unsigned char p_iModifier);
	virtual bool		OnLButtonUp(const CPnt& p_rxMousePos, unsigned char p_iModifier);
	virtual bool		OnMouseMove(const CPnt& p_rxMousePos, unsigned char p_iModifier);

	virtual bool		OnCharacterKey(int p_iKey, unsigned char p_iModifier);
	virtual bool		OnControlKey(int p_iKey, unsigned char p_iModifier);

	virtual bool		OnDeactivate();
	virtual bool		OnResize();
	virtual bool		OnVisualizationChange();

	int					GetItemAtCoordinates(CPnt p_xPos) const;
	void				Select(CPnt p_xPos, bool p_bUnselectRest = true);
	bool				GetMouseDown() const;

	virtual CStr		GetDebugString() const;

private:

	bool		m_bMouseDown;				///< true if LMB is down
	int			m_iLineHeight;				///< height of a single line of text; probably > font height
	CRct		m_xFrameSize;				///< size of frame around listbox
	int			m_iYOffset;					///< y scrolling offset (in pixels); to prevent selected line from being only half visible
	int			m_iFirstVisibleLine;		///< index of first line (= item) visible on the screen
	int			m_iNumVisibleLines;			///< number of lines visible at the moment
	int			m_iLastMouseMoveLine;		///< used in OnMouseMove

	int			m_iBitmapWidth;				///< space for bitmaps in front of each entry
	bool		m_bAllowScrollbar;			///< true, if the listbox should create a scrollbar if necessary
	bool		m_bAllowMultiSelection;		///< true, if more than one object may be selected at a given time
	int			m_iSelectedItem;			///< index of currently selected item (if multiselect: least recently selected item)
	int			m_iCurrentItem;				///< index of keyboard cursor item
	int			m_iSelStartItem;			///< item where multi-selection started
	int			m_iNumSelectedItems;		///< number of currently selected items
	int			m_iScrollTimer;				///< timer for scrolling if mouse button is held outside window

	CScrollBar*	m_pxScrollbar;				///< pointer to child scrollbar control or 0 if no scrollbar present

	CFunctionPointer1<CListBox*>	m_xOnSelectCallback;	///< Callbackfunktion bei jeder Selektionsänderung

	static const int m_iScrollSpeed = 100;

	/// local class for list entries
	class CEntry 
	{
	public:
		CEntry()		{ m_pxBitmap = 0;  m_bSelected = false; m_pItemData = NULL; }

		bool operator>(const CEntry& p_rxOther)	{return m_sString >  p_rxOther.m_sString;}
		bool operator==(const CEntry& p_rxOther)	{return m_sString == p_rxOther.m_sString;}

		CStr			m_sString;				///< label text of entry
		const CBitmap*	m_pxBitmap;				///< bitmap icon of entry
		bool			m_bSelected;			///< true if selected, false if not
		void*			m_pItemData;			///< data that belongs to entry
	};

	CDynArray<CEntry> m_axEntries;				///< array with items in list
};

#include "listbox.inl"

static const char* msgListBoxChanged = "LBoxChng";
class CListBoxChangedMsg : public CMessage
{ public: CListBoxChangedMsg(WHDL hWnd) : CMessage(msgListBoxChanged, false, true, hWnd)	{} };


static const char* msgListBoxDoubleClicked = "LBoxDClk";
class CListBoxDoubleClickedMsg : public CMessage
{ public: CListBoxDoubleClickedMsg(WHDL hWnd) : CMessage(msgListBoxDoubleClicked, false, true, hWnd)	{} };


} // namespace UILib



#endif // ifndef UILIB_LISTBOX_H_INCLUDED

