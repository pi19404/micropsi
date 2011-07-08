#include "stdafx.h"
#include "uilib/controls/spincontroltext.h"
#include "uilib/core/windowmanager.h"


namespace UILib
{

//---------------------------------------------------------------------------------------------------------------------
CSpinControlText::CSpinControlText()
{
	m_iSelectedItem = -1;
	m_pxEditCtrl->SetText("");
	SetEditable(false);
}


//---------------------------------------------------------------------------------------------------------------------
CSpinControlText::~CSpinControlText()
{
}


//---------------------------------------------------------------------------------------------------------------------
CSpinControlText*	
CSpinControlText::Create()						
{ 
	return new CSpinControlText(); 
}
	

//---------------------------------------------------------------------------------------------------------------------
/// \return Anzahl items in der Auswahlliste
int 
CSpinControlText::NumItems()
{
	return m_asStrings.Size();
}



//---------------------------------------------------------------------------------------------------------------------
/**
	Fügt der Auswahlliste ein neues Item hinzu
	\return Index des neuen Eintrags
*/
int	
CSpinControlText::AddItem(const CStr& p_sString)
{
	int i = m_asStrings.PushEntry(p_sString);
	if(i == 0  &&  m_pxEditCtrl->GetText().IsEmpty())
	{
		Select(i);
	}

    this->OnResize();

	return i;
}


//---------------------------------------------------------------------------------------------------------------------
/**
	Liefert ein Item zurück

	\param p_iIndex		Index des Items (0 == erstes)
	\return				Item als String
*/
CStr 
CSpinControlText::GetItem(int p_iIndex)
{
	assert(p_iIndex >=0  &&  p_iIndex < (int) m_asStrings.Size());

	if(p_iIndex >=0  &&  p_iIndex < (int) m_asStrings.Size())
	{
		return m_asStrings[p_iIndex];
	}

	return "";
}


//---------------------------------------------------------------------------------------------------------------------
/**
	Ermittelt den Index eines Items in der Auswahlliste

	\param p_sString	String, nach dem gesucht werden soll
	\return				Index des ersten passenden Items; -1 wenn nichts gefunden
*/
int 
CSpinControlText::FindItem(const CStr& p_sString)
{
	return m_asStrings.Find(p_sString);
}


//---------------------------------------------------------------------------------------------------------------------
/**
	Löscht ein Item aus der Auswahlliste
	\param p_iIndex		Index des Items (0 == erstes)
	\return				Anzahl übrige Items nach dem Löschen
*/
int 
CSpinControlText::DeleteItem(int p_iIndex)
{
	assert(p_iIndex >=0  &&  p_iIndex < (int) m_asStrings.Size());

	if(p_iIndex >=0  &&  p_iIndex < (int) m_asStrings.Size())
	{
		m_asStrings.DeleteEntry(p_iIndex);
		this->OnResize();
	}

	return m_asStrings.Size();
}


//---------------------------------------------------------------------------------------------------------------------
/**
	\return Index des aktuell gewählten Items oder -1 wenn es keine Auswahl gibt
	oder der User einen Text getippt hat, der nicht in der Auswahlliste ist
*/
int	
CSpinControlText::GetSelectedItem() const
{
	return m_iSelectedItem;
}


//---------------------------------------------------------------------------------------------------------------------
/**
	\return aktuell gewähltes Item als Sting
*/
CStr 
CSpinControlText::GetText() const
{
	return m_pxEditCtrl->GetText();
}


//---------------------------------------------------------------------------------------------------------------------
/**
	Wählt ein Item an
	\param p_iIndex		Index des Items (0 == erstes)
	\return				Index der neuen Wahl
*/
int 
CSpinControlText::Select(int p_iIndex)
{
	if(NumItems() == 0)
	{
		if(m_iSelectedItem != -1)
		{
			__super::OnChange();			   
		}
		return m_iSelectedItem = -1;
	}

	p_iIndex = min(p_iIndex, (int) m_asStrings.Size()-1);
	p_iIndex = max(p_iIndex, 0);

	if(p_iIndex == m_iSelectedItem)
	{
		return m_iSelectedItem;
	}
	
	m_iSelectedItem = p_iIndex;
	m_pxEditCtrl->SetText(m_asStrings[m_iSelectedItem]);
	InvalidateWindow();
	__super::OnChange();			   

	return m_iSelectedItem;
}


//---------------------------------------------------------------------------------------------------------------------
/**
	Wählt nächstes Item (ausgehend von der aktuellen Position), das mit dem 
	übergebenen Zeichen beginnt (Groß- und kleinschreibung wird ignoriert.
*/
bool 
CSpinControlText::SelectNextItemBeginningWithChar(int p_iChar)
{
	if(p_iChar == 0)
	{
		return false;
	}

	p_iChar = toupper(p_iChar);

	int i;
	for(i=m_iSelectedItem+1; i < (int) m_asStrings.Size(); ++i)
	{
		if(m_asStrings[i].GetLength() > 0  &&  toupper(m_asStrings[i].GetAt(0)) == p_iChar)
		{
			Select(i);
			return true;
		}
	}
	for(i=0; i<m_iSelectedItem; ++i)
	{
		if(m_asStrings[i].GetLength() > 0  &&  toupper(m_asStrings[i].GetAt(0)) == p_iChar)
		{
			Select(i);
			return true;
		}
	}

	return false;
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CSpinControlText::HandleMsg(const CMessage& p_rxMessage)
{
	if(p_rxMessage == msgEditControlChanged)
	{
		if(p_rxMessage.GetWindow() == m_pxEditCtrl->GetWHDL())
		{
			m_iSelectedItem = FindItem(m_pxEditCtrl->GetText());
			return true;
		}
	}

	return __super::HandleMsg(p_rxMessage);
}


//---------------------------------------------------------------------------------------------------------------------
void 
CSpinControlText::Up()
{
	if(m_iSelectedItem <= 0)
	{
		Select(0);
	}
	else
	{
		Select(GetSelectedItem() -1);
	}
}


//---------------------------------------------------------------------------------------------------------------------
void 
CSpinControlText::Down()
{
	if(m_iSelectedItem < 0)
	{
		Select(NumItems()-1);
	}
	else
	{
		Select(GetSelectedItem() +1);
	}
}

//---------------------------------------------------------------------------------------------------------------------


} //namespace UILib


