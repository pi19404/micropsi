
#pragma once 
#ifndef UILIB_COMBOBOX_H_INCLUDED 
#define UILIB_COMBOBOX_H_INCLUDED

#include "uilib/core/window.h"
#include "uilib/controls/button.h"
#include "uilib/controls/editcontrol.h"
#include "uilib/controls/listbox.h"
#include "uilib/core/itemdata.h"

namespace UILib
{

class CComboBox : public CWindow
{
public:

	/// erzeugt neue ComboBox
	static CComboBox*	Create();

	/// setzt Control automatisch auf optimale Größe
	virtual void	AutoSize(bool p_bMayShrink = true);

	/// liefert die Anzahl der Einträge in der Liste
	int				NumItems();

	/// liefert true, wenn dieses Fenster oder eines seiner Kinder den Fokus hat
	virtual bool	HasFocusOrChildHasFocus()	const;

	/// selektiert einen Eintrag aus der Liste
	int				Select(int p_iIndex);

	 /// fügt einen Eintrag zur Liste hinzu
	int				AddItem(const CStr& p_sString);

	 /// fügt einen Eintrag zur Liste hinzu
	int				AddItem(const CStr& p_sString, CBitmap* p_pxBmp);		

	 /// fügt einen Eintrag zur Liste hinzu
	int				AddItem(const CStr& p_sString, CItemData* p_pxItemData);

	 /// fügt einen Eintrag zur Liste hinzu
	int				AddItem(const CStr& p_sString, CItemData* p_pxItemData, CBitmap* p_pxBmp);		

	/// liefert einen Listeneintrag als String
	CStr			GetItem(int p_iIndex);

	/// findet einen Eintrag in der Liste und liefert den Index (oder -1 wenn nicht gefunden)
	int				FindItem(const CStr& p_sString);

	/// löscht einen Eintrag aus der Liste
	int				DeleteItem(int p_iIndex);

	/// liefert Userdaten eines Listeneintrages
	void*			GetItemDataEx(int p_iIndex) const;

	/// liefert Userdaten eines Listeneintrages
	CItemData*		GetItemData(int p_iIndex) const;

	/// setzt Userdaten auf einem Listeneintrag
	void			SetItemDataEx(int p_iIndex,void* p_pItemData);

	/// setzt Userdaten auf einem Listeneintrag
	void			SetItemData(int p_iIndex, CItemData* p_pxItemData);

	/// löscht alle Einträge aus der Liste
	void			Clear();

	/// setzt einen Text im EditControl
	void			SetText(CStr p_sText);		

	/// liefert den Text aus dem EditControl
	CStr			GetText() const;

	/// liefert Index des selektierten Listeneintrages oder -1, falls das EditControl etwas anderes enthält
	int				GetSelectedItem() const;

	/// liefert selektierten Eintrag als String (falls ein Eintrag selektiert ist)
	CStr			GetSelectedItemAsString() const;

	/// bestimmt, ob im EditControl Text getippt werden darf; ansonsten funktioniert nur Selektion über die Liste
	void			SetAllowAnyText(bool p_bAllowAnyText);

	/// liefert, ob im EditControl Text getippt werden darf; ansonsten funktioniert nur Selektion über die Liste
	bool			GetAllowAnyText() const;

	/// blendet die Pop-Up-Liste ein
	virtual void	ShowList();

	/// wird automatisch gerufen, wenn sich der Inhalt des Controls ändert
	virtual bool	OnChange();

	/// setzt Callbackfunktion für "Change"
	void			SetOnChangeCallback(CFunctionPointer1<CComboBox*>& rxCallback);

	/// setzt den Wert eines benannten Attributes
	virtual bool	SetAttrib(const CStr& p_rsName, const CStr& p_rsValue);

	/// liefert den Wert eines benannten Attributes
	virtual bool	GetAttrib(const CStr& p_rsName, CStr& po_srValue) const;

	/// bestimmt die maximale Höhe der Pop-Up-Liste in Pixeln
	void			SetMaxPopUpListHeight(int p_iH);

	/// liefert die maximale Höhe der Pop-Up-Liste in Pixeln
	int				GetMaxPopUpListHeight()	const;


protected:
	CComboBox();
	virtual ~CComboBox();

	/// eigene Unterklasse für Button
	class CComboBoxButton : public CBasicButton
	{
	public:		
		CComboBoxButton() {}
	protected:
		virtual void Paint(const CPaintContext& p_rxCtx);
	private:
		CComboBoxButton(const CComboBoxButton&) {}
		operator=(const CComboBoxButton&) {}
	};

	/// eigene Unterklasse für Liste
	class CComboBoxList : public CListBox
	{
	public:		
		CComboBoxList();
		CComboBox*		  m_pxParentDropList; 
		bool			  m_bListVisible;

		void		 Hide();

	protected:
		virtual bool OnControlKey(int p_iKey, unsigned char p_iModifier);

		virtual	bool OnDeactivate();		
		virtual bool OnLButtonUp(const CPnt& p_rxMousePos, unsigned char p_iModifier);
		virtual bool OnMouseMove(const CPnt& p_rxMousePos, unsigned char p_iModifier);

	private:
		CComboBoxList(const CComboBoxList&) {}
		operator=(const CComboBoxList&) {}
	};

	// eigene Klasse für Edit
	class CComboBoxEdit : public CEditControl
	{
	public: 
		CComboBoxEdit();
		CComboBox*		  m_pxParentDropList; 

	protected:
		virtual bool HandleMsg(const CMessage& p_rxMessage);
	private:
		CComboBoxEdit(const CComboBoxEdit&) {}
		operator=(const CComboBoxEdit&) {}
	};


	virtual CStr GetDebugString() const;

	virtual bool HandleMsg(const CMessage& p_rxMessage);
	virtual void Paint(const CPaintContext& p_rxCtx);

	virtual bool OnResize();
	virtual bool OnVisualizationChange();
	virtual	bool OnActivate();

	CComboBoxEdit		m_xEditCtrl;
	CComboBoxButton		m_xButton;
    CComboBoxList		m_xListCtrl;

	CFunctionPointer1<CComboBox*>	m_xOnChangeCallback;	///< Callbackfunktion bei jeder Veränderung

private:

	bool				m_bAllowAnyText;		///< false: nur Listeneinträge können gewählt werden; true: man kann im Editcontrol tippen
	int					m_iMaxListHeight;		///< Maximale Höhe der Pop-Up-Liste
	CRct				m_xFrameSize;			///< Größe des Rahmens um die Liste

	CComboBox(const CComboBox&) {}
	operator=(const CComboBox&) {}

	friend class CComboBoxList;
};

#include "combobox.inl"

static const char* msgComboBoxChanged = "CombChng";
class CComboBoxChangedMsg : public CMessage
{ public: CComboBoxChangedMsg(WHDL hWnd) : CMessage(msgComboBoxChanged, false, true, hWnd)	{} };


} // namespace UILib

#endif

