#include "stdafx.h"
#include "uilib/controls/listbox.h"
#include "uilib/core/windowmanager.h"	
#include "uilib/core/visualizationfactory.h"
#include "uilib/core/virtualkeycodes.h"

namespace UILib
{

	
//---------------------------------------------------------------------------------------------------------------------
CListBox::CListBox()
{
	m_iLineHeight			= 0;
	m_bMouseDown			= false;
	m_iSelectedItem			= -1;
	m_iSelStartItem			= -1;
	m_iCurrentItem			= -1;
	m_iNumSelectedItems		= 0;
	m_iYOffset				= 0;
	m_iFirstVisibleLine		= 0;
	m_iNumVisibleLines		= 0;
    m_bAllowScrollbar		= true;
	m_bAllowMultiSelection	= false;
	m_pxScrollbar			= 0;
	m_iBitmapWidth			= 0;
	m_iScrollTimer			= -1;
	m_iLastMouseMoveLine	= -1;

	SetSize(100, 150);
}



//---------------------------------------------------------------------------------------------------------------------
CListBox::~CListBox()
{
	if(m_iScrollTimer != -1)
	{
		CWindowMgr::Get().UnsetTimer(m_iScrollTimer);
		m_iScrollTimer = -1;
	}
}


//---------------------------------------------------------------------------------------------------------------------
CListBox* 
CListBox::Create()
{
	return new CListBox();
}


//---------------------------------------------------------------------------------------------------------------------
int 
CListBox::NumItems() const
{
	return m_axEntries.Size();
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CListBox::IsEmpty() const
{
	return NumItems() == 0;
}


//---------------------------------------------------------------------------------------------------------------------
void 
CListBox::Clear()
{
	Select(-1);
	m_axEntries.Clear();	
	this->OnResize();
	InvalidateWindow();
}



//---------------------------------------------------------------------------------------------------------------------
int	
CListBox::AddItem(const CStr& p_sString)
{
	return AddItem(p_sString, NULL, NULL);
}



//---------------------------------------------------------------------------------------------------------------------
int	
CListBox::AddItem(const CStr& p_sString, const CBitmap* p_pxBitmap)
{
	return AddItem(p_sString, NULL, p_pxBitmap);
}


//---------------------------------------------------------------------------------------------------------------------
int 
CListBox::AddItem(const CStr& p_sString, CItemData* p_pxItemData)
{
	return AddItem(p_sString, p_pxItemData, NULL);
}


//---------------------------------------------------------------------------------------------------------------------
int 
CListBox::AddItem(const CStr& p_sString, CItemData* p_pxItemData, const CBitmap* p_pxBitmap)
{
	int i = m_axEntries.PushEntry();
	m_axEntries[i].m_sString = p_sString;
	m_axEntries[i].m_pxBitmap = p_pxBitmap;
	SetItemData(i,p_pxItemData);
	if(p_pxBitmap)
	{
		m_iBitmapWidth = max(m_iBitmapWidth, p_pxBitmap->GetSize().cx);
		m_iLineHeight  = max(m_iLineHeight, p_pxBitmap->GetSize().cy);
	}
	this->OnResize();

	if(NumItems() == 1)
	{
		Select(0);
	}

	InvalidateWindow();
	return i;
}


//---------------------------------------------------------------------------------------------------------------------
CStr 
CListBox::GetItem(int p_iIndex) const
{
	assert(p_iIndex >=0  &&  p_iIndex < (int) m_axEntries.Size());	// index out of bounds

	if(p_iIndex >=0  &&  p_iIndex < (int) m_axEntries.Size())
	{
		return m_axEntries[p_iIndex].m_sString;
	}

	return "";
}


//---------------------------------------------------------------------------------------------------------------------
void 
CListBox::SetItemText(int p_iIndex, const CStr& p_rsString)
{
	assert(p_iIndex >=0  &&  p_iIndex < (int) m_axEntries.Size()); // index out of bounds

	if(p_iIndex >=0  &&  p_iIndex < (int) m_axEntries.Size())
	{
		m_axEntries[p_iIndex].m_sString = p_rsString;
		InvalidateWindow();
	}
}



//---------------------------------------------------------------------------------------------------------------------
int 
CListBox::FindItem(const CStr& p_sString) const
{
	for(unsigned int i=0; i<m_axEntries.Size(); ++i)
	{
		if(m_axEntries[i].m_sString == p_sString)
		{
			return i;
		}
	}
	return -1;
}


//---------------------------------------------------------------------------------------------------------------------
int 
CListBox::DeleteItem(int p_iIndex)
{
	assert(p_iIndex >=0  &&  p_iIndex < (int) m_axEntries.Size());	// index out of bounds

	if(p_iIndex >=0  &&  p_iIndex < (int) m_axEntries.Size())
	{
		UnSelect(p_iIndex);
		m_axEntries.DeleteEntry(p_iIndex);

		this->OnResize();
		InvalidateWindow();
	}

	return m_axEntries.Size();
}



//---------------------------------------------------------------------------------------------------------------------
/// \return item data that belongs to item at index \a p_iIdx
void* 
CListBox::GetItemDataEx(int p_iIndex) const
{
	assert(p_iIndex >=0  &&  p_iIndex < (int) m_axEntries.Size());	// index out of bounds

	if(p_iIndex >=0  &&  p_iIndex < (int) m_axEntries.Size())
	{
		return m_axEntries[p_iIndex].m_pItemData;
	}

	return NULL;
}


//---------------------------------------------------------------------------------------------------------------------
/// sets item data \a p_pxItemData to item at index \a p_iIdx
void 
CListBox::SetItemDataEx(int p_iIndex,void* p_pItemData)
{
	assert(p_iIndex >=0  &&  p_iIndex < (int) m_axEntries.Size());	// index out of bounds

	if(p_iIndex >=0  &&  p_iIndex < (int) m_axEntries.Size())
	{
		m_axEntries[p_iIndex].m_pItemData = p_pItemData;
	}
}


//---------------------------------------------------------------------------------------------------------------------
///	sets item data \a p_pxItemData to item at index \a p_iIndex
void CListBox::SetItemData(int p_iIndex, CItemData* p_pxItemData)
{
	SetItemDataEx(p_iIndex, p_pxItemData);
}



//---------------------------------------------------------------------------------------------------------------------
/// \return index of selected item; -1 if no item is selected (list empty)
int	
CListBox::GetSelectedItem() const
{
	return m_iSelectedItem;
}



//---------------------------------------------------------------------------------------------------------------------
/// \return string of currently selected item
CStr 
CListBox::GetSelectedItemAsString() const
{
	if(m_iSelectedItem < 0)
	{
		return "";
	}
	else
	{
		return m_axEntries[m_iSelectedItem].m_sString;
	}
}


//---------------------------------------------------------------------------------------------------------------------
/// \return true if item is selected
bool CListBox::IsItemSelected(int p_iIndex) const
{
	assert(p_iIndex >=0  &&  p_iIndex < (int) m_axEntries.Size());	// index out of bounds :)

	if(p_iIndex >=0  &&  p_iIndex < (int) m_axEntries.Size())
	{
		return m_axEntries[p_iIndex].m_bSelected;
	}
	else
	{
		return false;
	}
}


//---------------------------------------------------------------------------------------------------------------------
/// starts iteration of selected items
void 
CListBox::StartIterateSelectedItems(int& po_iIterator) const 
{
	po_iIterator = -1;
}



//---------------------------------------------------------------------------------------------------------------------
bool 
CListBox::IterateSelectedItems(int& po_iIterator) const
{
	po_iIterator++;
	if(po_iIterator < 0)
	{
		return false;
	}

	while(po_iIterator < (int) m_axEntries.Size()  &&  !m_axEntries[po_iIterator].m_bSelected)
	{
		po_iIterator++;
	}
	
	return po_iIterator < (int) m_axEntries.Size();
}



//---------------------------------------------------------------------------------------------------------------------
int 
CListBox::Select(int p_iIndex)
{
	if(p_iIndex < 0  ||  NumItems() == 0)	
	{ 
		if(m_iSelectedItem >= 0)
		{
			RemoveSelection();
		}
		return m_iSelectedItem;
	}

	p_iIndex = clamp(p_iIndex, 0, (int) m_axEntries.Size()-1);

	if(GetNumberOfSelectedItems() == 1  &&  p_iIndex == m_iSelectedItem)
	{
		return m_iSelectedItem;
	}
	
	RemoveSelectionNoMsg();	
	m_iSelectedItem = p_iIndex;
	m_axEntries[m_iSelectedItem].m_bSelected = true;
	m_iNumSelectedItems = 1;
	OnSelect();

	ShowSelection();
	InvalidateWindow();

	return m_iSelectedItem;
}


//---------------------------------------------------------------------------------------------------------------------
/** 
	select object with index; you may chose to deselect everything else or keep previous selection 
	(if multiselect is allowed)

	\param p_iIndex			index of item to be selected
	\param p_bUnselectRest	true: deselect all other items; false: add this item to current selection (only if multiselect is enabled)
	\return number of selected items
*/
int 
CListBox::Select(int p_iIndex, bool p_bUnselectRest)
{
	if(p_bUnselectRest || !m_bAllowMultiSelection)
	{
		Select(p_iIndex);
		return m_iNumSelectedItems;
	}

	if(m_axEntries[p_iIndex].m_bSelected == false)
	{
		m_axEntries[p_iIndex].m_bSelected = true;
		m_iNumSelectedItems++;	
		m_iSelectedItem = p_iIndex;
		ShowSelection();
		InvalidateWindow();
		OnSelect();
	}

	return m_iNumSelectedItems;
}


//---------------------------------------------------------------------------------------------------------------------
/**
	param select everything from an item to another one (order does not matter)
	will clear selection beforehand
	can only be used if multiselection is allowed
*/
void 
CListBox::Select(int p_iFrom, int p_iTo)
{
	if(!m_bAllowMultiSelection)
	{
		return;
	}

	int iStart, iEnd;
	if(p_iFrom < p_iTo)
	{
		iStart = p_iFrom; 
		iEnd = p_iTo;
	}
	else
	{
		iEnd = p_iFrom; 
		iStart = p_iTo;
	}

	RemoveSelectionNoMsg();
	int i;
	for(i=iStart; i<=iEnd; ++i)
	{
		if(m_axEntries[i].m_bSelected == false)
		{
			m_axEntries[i].m_bSelected = true;
			m_iNumSelectedItems++;	
		}
		m_iSelectedItem = iEnd;
	}
	ShowSelection();
	InvalidateWindow();
	OnSelect();
}



//---------------------------------------------------------------------------------------------------------------------
int	
CListBox::UnSelect(int p_iIndex)
{
	assert(p_iIndex >=0  &&  p_iIndex < (int) m_axEntries.Size());	// index out of bounds

	if(p_iIndex >=0  &&  p_iIndex < (int) m_axEntries.Size())
	{
		if(m_bAllowMultiSelection)
		{
			// mit Multiselection

			if(m_axEntries[p_iIndex].m_bSelected)
			{
				m_axEntries[p_iIndex].m_bSelected = false;
				m_iNumSelectedItems--;

				if(m_iSelectedItem == p_iIndex)
				{
					if(m_iNumSelectedItems > 0)
					{
						int i;
						for(i=m_axEntries.Size()-1; i>=0; --i)
						{
							m_iSelectedItem = -1;
							if(m_axEntries[i].m_bSelected)
							{
								m_iSelectedItem = i;
								break;
							}
						}
					}
					else
					{
						m_iSelectedItem = -1;
					}
				}

				InvalidateWindow();
				OnSelect();
			}
		}
		else
		{
			// ohne Multiselection

			if(m_iSelectedItem == p_iIndex)
			{
				RemoveSelection();
			}
		}
	}

	return m_iNumSelectedItems;
}



//---------------------------------------------------------------------------------------------------------------------
void 
CListBox::RemoveSelection()
{
	if(m_iNumSelectedItems > 0)
	{
		RemoveSelectionNoMsg();
		OnSelect();
	}
}



//---------------------------------------------------------------------------------------------------------------------
void 
CListBox::RemoveSelectionNoMsg()
{
	if(m_iNumSelectedItems > 0)
	{
		if(m_bAllowMultiSelection)
		{
			for(unsigned int i=0; i<m_axEntries.Size(); ++i)
			{
				m_axEntries[i].m_bSelected = false;
			}
		}
		else
		{
			m_axEntries[m_iSelectedItem].m_bSelected = false;
		}

		m_iNumSelectedItems = 0;
		m_iSelectedItem = -1; 
		InvalidateWindow();
	}
}



//---------------------------------------------------------------------------------------------------------------------
/// scroll list so selection is visible
void 
CListBox::ShowSelection()
{
	if(m_iNumVisibleLines > 0)
	{
		if(m_iSelectedItem >= m_iFirstVisibleLine+m_iNumVisibleLines)
		{
			m_iFirstVisibleLine = m_iSelectedItem - m_iNumVisibleLines;
			int iHeight = GetSize().cy - m_xFrameSize.top - m_xFrameSize.bottom;
			if(iHeight > 0  &&  m_iLineHeight > 0)
			{
				// selected line is at bottom of box - make sure it is completely visible
				m_iYOffset = - (m_iLineHeight - (iHeight % m_iLineHeight));
			}
		} 
		else if(m_iSelectedItem <= m_iFirstVisibleLine)
		{
			// selected line is at top of box - make sure it is completely visibly
			m_iFirstVisibleLine = m_iSelectedItem;
			m_iYOffset = 0;
		}
	}

	if(m_pxScrollbar)
	{
		m_pxScrollbar->SetScrollPos(m_iFirstVisibleLine);
	}
}


//---------------------------------------------------------------------------------------------------------------------
/**
	Select next item (from current position) that begins with a given character (case insensitive).
	Selection does not Select if there is no match.

	\param p_iChar character
	\return true, if a match was found 
*/
bool 
CListBox::SelectNextItemBeginningWithChar(int p_iChar)
{
	if(p_iChar == 0)
	{
		return false;
	}

	p_iChar = toupper(p_iChar);

	int i;
	for(i=m_iSelectedItem+1; i < (int) m_axEntries.Size(); ++i)
	{
		if(m_axEntries[i].m_sString.GetLength() > 0  &&  toupper(m_axEntries[i].m_sString.GetAt(0)) == p_iChar)
		{
			Select(i);
			return true;
		}
	}
	for(i=0; i<m_iSelectedItem; ++i)
	{
		if(m_axEntries[i].m_sString.GetLength() > 0  &&  toupper(m_axEntries[i].m_sString.GetAt(0)) == p_iChar)
		{
			Select(i);
			return true;
		}
	}

	return false;
}


//---------------------------------------------------------------------------------------------------------------------
/**
	Select first item that starts with given string. Selection does not Select if there is no match 
	\return true, if a match was found
*/
bool 
CListBox::SelectItemBeginningWithString(const CStr& p_sString)
{
	int i, len = p_sString.GetLength();

	if(len == 0)
	{
		return false;
	}

	for(i=0; i < (int) m_axEntries.Size(); ++i)
	{
		if(m_axEntries[i].m_sString.Left(len) == p_sString)
		{
			Select(i);
			return true;
		}
	}

	return false;
}


//---------------------------------------------------------------------------------------------------------------------
///	get index of items under given (local) window coordinates
int 
CListBox::GetItemAtCoordinates(CPnt p_xPos) const
{
	int i = m_iFirstVisibleLine + ((p_xPos.y - m_xFrameSize.top - m_iYOffset) / m_iLineHeight);
	if(i >= (int) m_axEntries.Size())
	{
		return -1;
	}
	else
	{
		return i;
	}
}



//---------------------------------------------------------------------------------------------------------------------
/// select item corresponding to cursor position
void 
CListBox::Select(CPnt p_xPos, bool p_bUnselectRest)
{
	if(p_xPos.y >= 0  &&  p_xPos.y < GetSize().cy)
	{
		Select(GetItemAtCoordinates(p_xPos), p_bUnselectRest);
	}
}



//---------------------------------------------------------------------------------------------------------------------
/// allow scrollbar if number of items is greater than window size allows - yes or no
void 
CListBox::SetAllowScrollBar(bool p_bAllowScrollbar)
{
	m_bAllowScrollbar = p_bAllowScrollbar;
	this->OnResize();
}



//---------------------------------------------------------------------------------------------------------------------
/// allow / disallow multiselection
void 
CListBox::SetAllowMultiSelection(bool p_bAllowMultiSelection)
{
	if(p_bAllowMultiSelection != m_bAllowMultiSelection)
	{
		if(!p_bAllowMultiSelection)
		{
			int i = m_iSelectedItem; 
			RemoveSelection();
			Select(i);
			InvalidateWindow();
		}
		m_bAllowMultiSelection = p_bAllowMultiSelection;
	}
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CListBox::HandleMsg(const CMessage& p_rxMessage)
{
	if(p_rxMessage == msgMouseLeftButtonDown)
	{
		return this->OnLButtonDown(p_rxMessage.GetPos(), p_rxMessage.GetKeyModifier());
	}
	else if(p_rxMessage == msgMouseLeftButtonUp)
	{
		return this->OnLButtonUp(p_rxMessage.GetPos(), p_rxMessage.GetKeyModifier());
	}
	else if(p_rxMessage == msgMouseMove)
	{
		return this->OnMouseMove(p_rxMessage.GetPos(), p_rxMessage.GetKeyModifier());
	}
	else if(p_rxMessage == msgKeyDown  ||  p_rxMessage == msgKeyUp)
	{
		// this control does key processing on its own; keyup and keydown messages should not bubble to parent
		return true;		
	}
	else if(p_rxMessage == msgScrollBarChanged)
	{
		if(m_pxScrollbar)
		{
			m_iFirstVisibleLine = m_pxScrollbar->GetScrollPos();
			if(m_iFirstVisibleLine == 0)
			{
				// top line reached - make sure it is completely visible
				m_iYOffset = 0;
			}
			else if(m_pxScrollbar->GetScrollRange() == m_pxScrollbar->GetScrollPos())
			{
				// bottom line reached - make sure it is completely visible
				int iHeight = GetSize().cy - m_xFrameSize.top - m_xFrameSize.bottom;
				m_iYOffset = - (m_iLineHeight - (iHeight % m_iLineHeight));
			}
			InvalidateWindow();
		}
		return true;
	}
	else if(p_rxMessage == msgTimer)
	{
		// slowly scroll if mouse button is held down and mouse is outside window

		if(p_rxMessage.GetTimerID() == m_iScrollTimer)
		{
			CWindowMgr& wm = CWindowMgr::Get();
			CPnt xMousePos = wm.GetMousePos();
			wm.ToClientPos(GetWHDL(), xMousePos);

			if(xMousePos.y < 0  &&  m_iSelectedItem > 0)
			{
				Select(m_iSelectedItem-1);
			}
			else if(xMousePos.y >= GetSize().cy)
			{
				Select(m_iSelectedItem+1);
			}
			return true;
		}
		return __super::HandleMsg(p_rxMessage);
	}
	else
	{
		return __super::HandleMsg(p_rxMessage);
	}
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CListBox::OnLButtonDown(const CPnt& p_rxMousePos, unsigned char p_iModifier)
{
	if(GetDisabled())
	{
		return true;
	}

	CPnt xMousePos = p_rxMousePos;
	CWindowMgr& wm = CWindowMgr::Get();
	wm.ToClientPos(GetWHDL(), xMousePos);

	// select clicked line
	int iItem = GetItemAtCoordinates(xMousePos);
	if(iItem >= 0)
	{
		m_iCurrentItem = iItem;

		if(p_iModifier & KM_CONTROL)
		{
			if(IsItemSelected(iItem)  &&  m_bAllowMultiSelection)
			{
				UnSelect(iItem);
			}
			else
			{
				Select(iItem, !m_bAllowMultiSelection);
			}
			m_iSelStartItem = iItem;
		}
		else if(p_iModifier & KM_SHIFT)
		{
			if(m_bAllowMultiSelection)
			{
				if(m_iSelStartItem >= 0  &&  m_iSelStartItem < NumItems()  &&  m_iSelStartItem != iItem)
				{
					Select(m_iSelStartItem, iItem);
				}
			}
			else
			{
				Select(iItem);
				m_iSelStartItem = iItem;
			}
		}
		else
		{
			Select(iItem);
			m_iSelStartItem = iItem;
		}
	}

	m_bMouseDown = true;
	wm.SetCapture(this);
	InvalidateWindow();
	m_iScrollTimer = SetTimer(m_iScrollSpeed, true);
	
	return true;
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CListBox::OnLButtonUp(const CPnt& p_rxMousePos, unsigned char p_iModifier)
{
	if(m_bMouseDown)
	{
		CWindowMgr& wm = CWindowMgr::Get();
		m_bMouseDown = false;
		wm.ReleaseCapture(this);
		if(m_iScrollTimer != -1)
		{
			wm.UnsetTimer(m_iScrollTimer);
			m_iScrollTimer = -1;
		}
		InvalidateWindow();
	}
	return true;
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CListBox::OnMouseMove(const CPnt& p_rxMousePos, unsigned char p_iModifier)
{
	if(m_bMouseDown)
	{
		CPnt xMousePos = p_rxMousePos;
		CWindowMgr::Get().ToClientPos(GetWHDL(), xMousePos);

		int iItem = GetItemAtCoordinates(xMousePos);
		if(iItem >= 0)
		{
			if(m_bAllowMultiSelection  &&  p_iModifier & KM_SHIFT  &&  
			   m_iSelStartItem >= 0  &&  m_iSelStartItem < NumItems())
			{
				m_iCurrentItem = iItem;
				Select(m_iSelStartItem, iItem);
			}
			else
			{
				Select(iItem);
			}
		}
		m_iLastMouseMoveLine = iItem;
	}
	return true;
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CListBox::OnCharacterKey(int p_iKey, unsigned char p_iModifier)
{
	SelectNextItemBeginningWithChar(p_iKey);
	return true;
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CListBox::OnControlKey(int p_iKey, unsigned char p_iModifier)
{
	if(GetDisabled())
	{
		return true;
	}

	// sicherstellen, dass Tastaturfokus noch auf etwas sinnvollem liegt...
	// ist viel einfacher hier zu machen, als es bei jedem Add/Delete upzudaten
	if(m_iCurrentItem < 0  &&  m_axEntries.Size() > 0)
	{
		m_iCurrentItem = 0;
	}

	switch(p_iKey) 
	{
		case VKey_Escape:
			if(m_bMouseDown)
			{
				m_bMouseDown = false;
				CWindowMgr::Get().ReleaseCapture(this);
			}
			break;

		case VKey_Down:
			{
				m_iCurrentItem++;
				m_iCurrentItem = min(m_iCurrentItem, NumItems()-1);

				if(p_iModifier & KM_SHIFT  &&  m_bAllowMultiSelection)
				{
					Select(m_iSelStartItem, m_iCurrentItem);
				}
				else
				{
					m_iSelStartItem = m_iCurrentItem;
					Select(m_iCurrentItem);
				}
			}
			break;

		case VKey_Up:
			{
				m_iCurrentItem--;
				m_iCurrentItem = max(m_iCurrentItem, 0);

				if(p_iModifier & KM_SHIFT  &&   m_bAllowMultiSelection)
				{
					Select(m_iSelStartItem, m_iCurrentItem);
				}
				else
				{
					m_iSelStartItem = m_iCurrentItem;
					Select(m_iCurrentItem);
				}
			}
			break;

		case VKey_Home:
			Select(0);
			break;

		case VKey_End:
			Select(m_axEntries.Size()-1);
			break;
	}

	return true;
}


//---------------------------------------------------------------------------------------------------------------------
void 
CListBox::Paint(const CPaintContext& p_rxCtx)
{	
	CVisualization* v = CVisualizationFactory::Get().GetVisualization(p_rxCtx.GetDevice(), GetVisualizationType());

	CRct rFrameSize = v->GetFrameSize(CVisualization::FT_TextBox);
	CRct xInterior = CRct(GetRect().left + rFrameSize.left, GetRect().top + rFrameSize.top, GetRect().right - rFrameSize.right, GetRect().bottom - rFrameSize.bottom);

	v->DrawBackground(p_rxCtx, xInterior, CVisualization::BG_Editable);

    CRct xLine = CRct(xInterior.left + m_iBitmapWidth +5, xInterior.top + m_iYOffset, xInterior.right -5, xInterior.top + m_iYOffset + m_iLineHeight);
	for(unsigned int i=m_iFirstVisibleLine; i<m_axEntries.Size(); ++i)
	{
		if(m_axEntries[i].m_bSelected  &&  !GetDisabled())
		{
			v->DrawBackground(p_rxCtx, CRct(xInterior.left, xLine.top, xInterior.right, xLine.bottom), CVisualization::BG_Selection);
			if(m_axEntries[i].m_pxBitmap)
			{
				p_rxCtx.Blit(CPnt((m_iBitmapWidth - m_axEntries[i].m_pxBitmap->GetSize().cx) / 2, 
					xLine.top + ((m_iLineHeight - m_axEntries[i].m_pxBitmap->GetSize().cy) /2)), 
					m_axEntries[i].m_pxBitmap, true);
			}
			v->DrawText(p_rxCtx, CVisualization::FONT_TextBox, xLine, COutputDevice::TA_Left, COutputDevice::TA_VCenter, m_axEntries[i].m_sString, CVisualization::TP_Selected);
		}
		else
		{
			if(m_axEntries[i].m_pxBitmap)
			{
				p_rxCtx.Blit(CPnt((m_iBitmapWidth - m_axEntries[i].m_pxBitmap->GetSize().cx) / 2, 
					xLine.top + ((m_iLineHeight - m_axEntries[i].m_pxBitmap->GetSize().cy) /2)), 
					m_axEntries[i].m_pxBitmap, true);
			}
			if(!GetDisabled())
			{
				v->DrawText(p_rxCtx, CVisualization::FONT_TextBox, xLine, COutputDevice::TA_Left, COutputDevice::TA_VCenter, m_axEntries[i].m_sString, CVisualization::TP_Normal);
			}
			else
			{
				v->DrawText(p_rxCtx, CVisualization::FONT_TextBox, xLine, COutputDevice::TA_Left, COutputDevice::TA_VCenter, m_axEntries[i].m_sString, CVisualization::TP_Disabled);
			}
		}
		xLine.top += m_iLineHeight;
		xLine.bottom += m_iLineHeight;
		if(xLine.top >= xInterior.bottom)
		{
			break;
		}
	}

	v->DrawFrame(p_rxCtx, GetRect(), CVisualization::FT_TextBox, GetDisabled());
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CListBox::OnDeactivate()
{
	if(m_iScrollTimer != -1)
	{
		CWindowMgr::Get().UnsetTimer(m_iScrollTimer);
		m_iScrollTimer = -1;
	}
	return true;
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CListBox::OnResize()
{
	int iHeight = GetSize().cy - m_xFrameSize.top - m_xFrameSize.bottom;
	if(m_iLineHeight > 0)
	{
		m_iNumVisibleLines = iHeight / m_iLineHeight;
		if(m_bAllowScrollbar  &&  m_iNumVisibleLines < (int) m_axEntries.Size())
		{
			// Fenster sollte einen Rollbalken haben

			if(m_pxScrollbar == 0)
			{
				m_pxScrollbar = CScrollBar::Create();
				AddChild(m_pxScrollbar->GetWHDL());
			}

			m_pxScrollbar->SetSize(CSize(0, GetSize().cy - m_xFrameSize.top - m_xFrameSize.bottom));
			m_pxScrollbar->SetPos(CPnt(GetSize().cx - m_xFrameSize.right - m_pxScrollbar->GetSize().cx, m_xFrameSize.top));
			m_pxScrollbar->SetScrollRange(m_axEntries.Size()-1);
			m_pxScrollbar->SetPageSize(m_iNumVisibleLines);
			m_pxScrollbar->SetScrollPos(m_iFirstVisibleLine);
		}
		else
		{
			// Fenster sollte *keinen* Rollbalken haben

			if(m_pxScrollbar)
			{
				RemoveChild(m_pxScrollbar->GetWHDL());
				m_pxScrollbar->Destroy();
				m_pxScrollbar = 0;
			}
		}
	}

	return this->__super::OnResize();
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CListBox::OnVisualizationChange()
{
	const COutputDevice* pxDevice = CWindowMgr::Get().GetDeviceConst(GetWHDL());
	if(pxDevice)
	{
		CVisualization* v	= CVisualizationFactory::Get().GetVisualization(pxDevice, GetVisualizationType());
		m_iLineHeight = (int) (v->GetFontMetrics(CVisualization::FONT_TextBox)->m_iHeight * 1.1f);
		m_xFrameSize  = v->GetFrameSize(CVisualization::FT_TextBox);
	}

	this->OnResize();
	return this->__super::OnVisualizationChange();
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CListBox::OnSelect()
{				
	CWindowMgr::Get().PostMsg(CListBoxChangedMsg(GetWHDL()), GetParent());
	if(m_xOnSelectCallback)
	{
		m_xOnSelectCallback(this);
	}
	return true;
}

//---------------------------------------------------------------------------------------------------------------------
CStr
CListBox::GetDebugString() const
{ 
	return "CListBox"; 
}

//---------------------------------------------------------------------------------------------------------------------
/**
	Setzt den Wert eines benannten Attributes. Siehe UIlib Doku für eine vollständige Liste der Attribute.
	\param	p_rsName	Name des Attributes
	\param	p_rsValue	neuer Wert
	\return	true, wenn erfolgreich
*/
bool 
CListBox::SetAttrib(const CStr& p_rsName, const CStr& p_rsValue)
{
	if(p_rsName == "allowscrollbar")	{ SetAllowScrollBar(p_rsValue.ToInt()!=0); return true; }
	if(p_rsName == "multiselect")		{ SetAllowMultiSelection(p_rsValue.ToInt()!=0); return true; }
	return __super::SetAttrib(p_rsName,p_rsValue);
}


//---------------------------------------------------------------------------------------------------------------------
/**
	liefert den Wert eines benannten Attributes. Siehe UILib Doku für eine vollständige Liste der Attribute. ues

	\param	p_rsName		Name des Attributes
	\param	po_srValue		Ausgabe-Parameter: String, der den Wert entgegennimmt
	\return true, wenn Wert zurückgeliefert wurde; false, wenn der Attributnamen nicht bekannt ist
*/
bool 
CListBox::GetAttrib(const CStr& p_rsName, CStr& po_srValue) const
{
	if(p_rsName=="allowscrollbar"){po_srValue=(m_bAllowScrollbar?"1":"0");return true;}
	if(p_rsName=="multiselect"){po_srValue=(m_bAllowMultiSelection?"1":"0");return true;}
	return __super::GetAttrib(p_rsName,po_srValue);
}
//---------------------------------------------------------------------------------------------------------------------

} //namespace UILib

