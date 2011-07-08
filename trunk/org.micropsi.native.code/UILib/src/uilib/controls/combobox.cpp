#include "stdafx.h"
#include "uilib/controls/combobox.h"
#include "uilib/core/windowmanager.h"
#include "uilib/core/visualizationfactory.h"
#include "uilib/core/virtualkeycodes.h"

namespace UILib
{

//---------------------------------------------------------------------------------------------------------------------
void 
CComboBox::CComboBoxButton::Paint(const CPaintContext& p_rxCtx)
{
	CVisualization::FrameType eFt = CVisualization::FT_BtnUp;
	if(!GetDisabled())
	{
		if(GetButtonDown()) { eFt = CVisualization::FT_BtnDown; }
	}
	CVisualization* v = CVisualizationFactory::Get().GetVisualization(p_rxCtx.GetDevice(), GetVisualizationType());
	v->DrawComboBoxButton(p_rxCtx, GetRect(), eFt);
}


//---------------------------------------------------------------------------------------------------------------------
CComboBox::CComboBoxList::CComboBoxList()
{ 
	m_pxParentDropList = 0; 
	m_bListVisible = false; 
	SetIgnoreModals(true);
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CComboBox::CComboBoxList::OnDeactivate()
{
	CWindowMgr& wm = CWindowMgr::Get();
	WHDL hWndActive = wm.GetFocusWindow();
	if(hWndActive)
	{
		CWindow* pxWndActive = wm.GetWindow(hWndActive);
		if(pxWndActive->IsChildOf(GetWHDL()))
		{
			// wenn wir den Fokus an unser eigenen Scrollbar verlieren, holen wir den Fokus wieder
			// wir müssen nämlich merken, wann wir den Fokus an ein fremdes Fenster verlieren.
			wm.BringWindowToTop(GetWHDL());
			return true;
		}

		if(m_pxParentDropList->m_xButton.HasFocusOrChildHasFocus())
		{
			wm.BringWindowToTop(GetWHDL(), false);
			return true;
		}

		if(!m_pxParentDropList->GetAllowAnyText()  &&
			m_pxParentDropList->HasFocusOrChildHasFocus())
		{
			wm.BringWindowToTop(GetWHDL(), true);
			return true;
		}
	}

	// Fokus an fremdes Fenster verloren!
	Hide();
	return true;
}		


//---------------------------------------------------------------------------------------------------------------------
bool 
CComboBox::CComboBoxList::OnLButtonUp(const CPnt& p_rxMousePos, unsigned char p_iModifier)
{
	if(GetMouseDown())
	{
		__super::OnLButtonUp(p_rxMousePos, p_iModifier);

		Hide();
		if(m_pxParentDropList)
		{
			if(GetSelectedItem() >= 0)
			{
				m_pxParentDropList->Select(GetSelectedItem());
			}
			CWindowMgr::Get().BringWindowToTop(m_pxParentDropList->GetWHDL(), true);
		}
	}

	return true;
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CComboBox::CComboBoxList::OnMouseMove(const CPnt& p_rxMousePos, unsigned char p_iModifier)
{
	CPnt xMousePos = p_rxMousePos;
	CWindowMgr::Get().ToClientPos(GetWHDL(), xMousePos);

	if(GetMouseDown())
	{
		if(xMousePos.y < 0)
		{
			Select(GetSelectedItem()-1);
		}
		else if(xMousePos.y >= GetSize().cy)
		{
			Select(GetSelectedItem()+1);
		}
		else
		{
		    Select(xMousePos);
		}
	}
	else
	{
	    Select(xMousePos);
	}
	return true;
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CComboBox::CComboBoxList::OnControlKey(int p_iKey, unsigned char p_iModifier)
{
	if(p_iKey == VKey_Escape)
	{
		CWindowMgr& wm = CWindowMgr::Get();
		if(m_pxParentDropList)
		{
			wm.BringWindowToTop(m_pxParentDropList->GetWHDL());
		}
		Hide();
		if(m_pxParentDropList)
		{
			wm.BringWindowToTop(m_pxParentDropList->GetWHDL(), true);
		}
	}
	else if (p_iKey == VKey_Return) 
	{
		CWindowMgr& wm = CWindowMgr::Get();
		if(m_pxParentDropList)
		{
			wm.BringWindowToTop(m_pxParentDropList->GetWHDL());
			m_pxParentDropList->SetText(GetSelectedItemAsString());
		}
		Hide();
		if(m_pxParentDropList)
		{
			wm.BringWindowToTop(m_pxParentDropList->GetWHDL(), true);
		}
	}

	return __super::OnControlKey(p_iKey, p_iModifier);
}


//---------------------------------------------------------------------------------------------------------------------
void 
CComboBox::CComboBoxList::Hide()
{
	__super::OnLButtonUp(CPnt(0, 0), 0);
	m_bListVisible = false;

	CWindowMgr::Get().RemoveTopLevelWindow(GetWHDL());
}



//---------------------------------------------------------------------------------------------------------------------
CComboBox::CComboBoxEdit::CComboBoxEdit()
{
	m_pxParentDropList = 0;
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CComboBox::CComboBoxEdit::HandleMsg(const CMessage& p_rxMessage)
{
	if(p_rxMessage == msgMouseLeftButtonDown)
	{
		if(!GetDisabled() &&  GetReadOnly())
		{
			if(m_pxParentDropList->m_xListCtrl.m_bListVisible)
			{
				m_pxParentDropList->m_xListCtrl.Hide();
			}
			else
			{
				m_pxParentDropList->ShowList();
			}
		}
	}

	return __super::HandleMsg(p_rxMessage);
}


//---------------------------------------------------------------------------------------------------------------------
CComboBox::CComboBox()
{
	m_xListCtrl.m_pxParentDropList = this;
	m_xEditCtrl.m_pxParentDropList = this;
	m_xEditCtrl.SetFrame(false);
	m_xEditCtrl.SetReadOnly(true);
	AddChild(m_xEditCtrl.GetWHDL());
	AddChild(m_xButton.GetWHDL());

	m_iMaxListHeight = 200;
	SetAllowAnyText(false);
	m_xEditCtrl.SetReadOnly(true);
}


//---------------------------------------------------------------------------------------------------------------------
CComboBox::~CComboBox()
{
}


//---------------------------------------------------------------------------------------------------------------------
CComboBox*	
CComboBox::Create()						
{ 
	return new CComboBox();
}


//---------------------------------------------------------------------------------------------------------------------
void 
CComboBox::AutoSize(bool p_bMayShrink)
{
}


//---------------------------------------------------------------------------------------------------------------------
bool	
CComboBox::HasFocusOrChildHasFocus() const		
{ 
	return __super::HasFocusOrChildHasFocus() ||  m_xListCtrl.HasFocusOrChildHasFocus(); 
}


//---------------------------------------------------------------------------------------------------------------------
int	
CComboBox::Select(int p_iIndex)
{
	int i = m_xListCtrl.Select(p_iIndex);
	m_xEditCtrl.SetText(m_xListCtrl.GetSelectedItemAsString());
	return i;
}


//---------------------------------------------------------------------------------------------------------------------
int	
CComboBox::AddItem(const CStr& p_sString)		
{
	return AddItem(p_sString,NULL,NULL);
}		


//---------------------------------------------------------------------------------------------------------------------
int	
CComboBox::AddItem(const CStr& p_sString, CBitmap* p_pxBmp)		
{
	return AddItem(p_sString,NULL,p_pxBmp);
}


//---------------------------------------------------------------------------------------------------------------------
int 
CComboBox::AddItem(const CStr& p_sString, CItemData* p_pxItemData)
{
	return AddItem(p_sString,p_pxItemData,NULL);
}


//---------------------------------------------------------------------------------------------------------------------
int 
CComboBox::AddItem(const CStr& p_sString, CItemData* p_pxItemData, CBitmap* p_pxBmp)
{
	int i = m_xListCtrl.AddItem(p_sString, p_pxItemData, p_pxBmp); 
	if(m_xListCtrl.NumItems() == 1)
	{
		Select(i);
	}
	return i; 
}


//---------------------------------------------------------------------------------------------------------------------
int	
CComboBox::DeleteItem(int p_iIndex)			
{ 
	int  iSel = m_xListCtrl.GetSelectedItem();
	CStr sSel = m_xListCtrl.GetSelectedItemAsString();
	int i = m_xListCtrl.DeleteItem(p_iIndex);
	
	// if currently selected item is deleted, make sure selection is valid afterwards
	if(iSel == p_iIndex)
	{
		SetText(sSel);
	}
	
	return i;
}


//---------------------------------------------------------------------------------------------------------------------
void 
CComboBox::SetItemData(int p_iIndex,CItemData* p_pxItemData)
{
	SetItemDataEx(p_iIndex, p_pxItemData);
}


//---------------------------------------------------------------------------------------------------------------------
void 
CComboBox::SetText(CStr p_sText)				
{ 
	int i = FindItem(p_sText);
	if(i>=0)
	{
		Select(i);
	}
	else
	{
		if(m_bAllowAnyText) 
		{
			m_xEditCtrl.SetText(p_sText);
		}
	}
}


//---------------------------------------------------------------------------------------------------------------------
void 
CComboBox::SetAllowAnyText(bool p_bAllowAnyText)
{
	if(m_bAllowAnyText != p_bAllowAnyText)
	{
		m_bAllowAnyText = p_bAllowAnyText;
		m_xEditCtrl.SetReadOnly(!m_bAllowAnyText);
		if(!m_bAllowAnyText  &&  GetSelectedItem() < 0)
		{
			Select(0);
		}
	}
}


//---------------------------------------------------------------------------------------------------------------------
void 
CComboBox::ShowList()
{
	CWindowMgr& wm = CWindowMgr::Get();
	wm.AddTopLevelWindow(m_xListCtrl.GetWHDL());
	m_xListCtrl.m_bListVisible = true;

	m_xListCtrl.SetSize( GetSize().cx, min(m_iMaxListHeight, m_xListCtrl.GetNeededHeight(m_xListCtrl.NumItems())) );

	CPnt xListBoxPos = GetAbsPos() + CPnt(0, m_xFrameSize.top + m_xEditCtrl.GetSize().cy);
	CWindow* pxRoot = GetRootWindow(); 
	if(pxRoot)
	{
		if(xListBoxPos.y + m_xListCtrl.GetSize().cy >= pxRoot->GetSize().cy)
		{
			// unter dem editcontrol ist nicht genug platz für die Liste... vielleicht darüber?
			int y = GetAbsPos().y + m_xFrameSize.top - m_xListCtrl.GetSize().cy;
			if(y >= 0)
			{
				// ja, dann machen wir es so
				xListBoxPos.y = y;
			}
		}
	}
	m_xListCtrl.SetPos(xListBoxPos);


	wm.BringWindowToTop(m_xListCtrl.GetWHDL());
}


//---------------------------------------------------------------------------------------------------------------------
bool	
CComboBox::OnChange()		
{ 
	CWindowMgr::Get().PostMsg(CComboBoxChangedMsg(GetWHDL()), GetParent());
	if(m_xOnChangeCallback)
	{
		m_xOnChangeCallback(this);
	}	
	return true; 
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CComboBox::HandleMsg(const CMessage& p_rxMessage)
{
	if(p_rxMessage == msgButtonClicked)
	{
		if(p_rxMessage.GetWindow() == m_xButton.GetWHDL())
		{
			if(m_xListCtrl.m_bListVisible)
			{
				m_xListCtrl.Hide();
			}
			else
			{
				ShowList();
			}
		}
		return true;
	}
	else if(p_rxMessage == msgEditControlChanged)
	{
		CStr sText = m_xEditCtrl.GetText();
		int i = m_xListCtrl.FindItem(sText);
		if(i >= 0)
		{
			m_xListCtrl.Select(i);
		}
		else
		{
			if(m_bAllowAnyText)
			{
				m_xListCtrl.Select(-1);
			}
			else
			{
				if(m_xListCtrl.NumItems() > 0  ||  m_xEditCtrl.GetText() != "")
				{
					m_xEditCtrl.SetText(m_xListCtrl.GetSelectedItemAsString());
				}
			}
		}
		OnChange();

		return true;
	}
	else if(p_rxMessage == msgEditControlUpdated)
	{
		if(m_xListCtrl.m_bListVisible)
		{
			m_xListCtrl.Hide();
		}
		return true;
	}
	else if(p_rxMessage == msgControlKey)
	{
		if(p_rxMessage.GetKey() == VKey_Down)
		{
			if(!m_xListCtrl.m_bListVisible)
			{
				ShowList();
			}
		}
		return true;
	}
	else
	{
		return __super::HandleMsg(p_rxMessage);
	}
}


//---------------------------------------------------------------------------------------------------------------------
void 
CComboBox::Paint(const CPaintContext& p_rxCtx)
{
	CVisualization* v = CVisualizationFactory::Get().GetVisualization(p_rxCtx.GetDevice(), GetVisualizationType());

	v->DrawFrame(p_rxCtx, GetRect(), CVisualization::FT_TextBox, GetDisabled());
}


//---------------------------------------------------------------------------------------------------------------------
CStr 
CComboBox::GetDebugString() const		
{ 
	return CStr("CComboBox Content = ") + GetText(); 
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CComboBox::OnResize()
{
	CSize xSize = GetSize();
	int iHeight = xSize.cy;
	iHeight -= (m_xFrameSize.top + m_xFrameSize.bottom);
	m_xButton.SetSize(CSize(iHeight*3/4, iHeight));

	CSize xEditSize = CSize(xSize.cx - m_xButton.GetSize().cx - m_xFrameSize.left - m_xFrameSize.right, 
						    xSize.cy - m_xFrameSize.top - m_xFrameSize.bottom);
	m_xEditCtrl.SetMinSize(xEditSize);
	m_xEditCtrl.SetMaxSize(xEditSize);

	m_xEditCtrl.SetPos(CPnt(m_xFrameSize.left, m_xFrameSize.top));
	m_xButton.SetPos(CPnt(m_xFrameSize.left + m_xEditCtrl.GetSize().cx, m_xFrameSize.top));

	return __super::OnResize();
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CComboBox::OnVisualizationChange()
{
	const COutputDevice* pxDevice = CWindowMgr::Get().GetDeviceConst(GetWHDL());
	if(pxDevice)
	{
		CVisualization* v = CVisualizationFactory::Get().GetVisualization(pxDevice, GetVisualizationType());
		m_xFrameSize = v->GetFrameSize(CVisualization::FT_TextBox);
	}

	AutoSize(false);

	this->OnResize();
	return this->__super::OnVisualizationChange();
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CComboBox::OnActivate()
{
	CWindowMgr::Get().BringWindowToTop(m_xEditCtrl.GetWHDL());
	return true;
}	


//---------------------------------------------------------------------------------------------------------------------
/**
	Setzt den Wert eines benannten Attributes. Siehe UIlib Doku für eine vollständige Liste der Attribute.
	\param	p_rsName	Name des Attributes
	\param	p_rsValue	neuer Wert
	\return	true, wenn erfolgreich
*/
bool 
CComboBox::SetAttrib(const CStr& p_rsName, const CStr& p_rsValue)
{
	if(p_rsName=="caption")	{ SetText(p_rsValue); return true; }
	return __super::SetAttrib(p_rsName, p_rsValue);
}


//---------------------------------------------------------------------------------------------------------------------
/**
	liefert den Wert eines benannten Attributes. Siehe UILib Doku für eine vollständige Liste der Attribute. ues

	\param	p_rsName		Name des Attributes
	\param	po_srValue		Ausgabe-Parameter: String, der den Wert entgegennimmt
	\return true, wenn Wert zurückgeliefert wurde; false, wenn der Attributnamen nicht bekannt ist
*/
bool 
CComboBox::GetAttrib(const CStr& p_rsName, CStr& po_srValue) const
{
	if(p_rsName=="caption")		{ po_srValue=GetText(); return true; }
	return __super::GetAttrib(p_rsName, po_srValue);
}
//---------------------------------------------------------------------------------------------------------------------

} // namespace UILib


