#include "stdafx.h"
#include "uilib/core/font.h"

namespace UILib
{

/**
	constructor
*/
CFont::CFont(int p_iHeight, const CStr& p_rsFaceName, Locale p_eLocale, Pitch p_ePitch, Weight p_eWeight, int p_iFlags)
{
	m_xFontMetrics.m_ePitch		= p_ePitch;
	m_xFontMetrics.m_eWeight	= p_eWeight;
	m_xFontMetrics.m_iFlags		= p_iFlags;
	m_eLocale					= p_eLocale;
	m_sFaceName					= p_rsFaceName;
}


/**
	destructor
*/
CFont::~CFont()
{
}


/** 
	get all information about this font as a string
*/
CStr CFont::GetInfoString() const
{
	return GetInfoString(m_xFontMetrics.m_iHeight, m_sFaceName, m_eLocale, m_xFontMetrics.m_ePitch, m_xFontMetrics.m_eWeight, m_xFontMetrics.m_iFlags);
}


/** 
	create an information string from the parameters
*/
CStr CFont::GetInfoString(int p_iHeight, const CStr& p_rsFaceName, Locale p_eLocale, Pitch p_ePitch, Weight p_eWeight, int p_iFlags)
{
	CStr s = CStr::Create("Font %s %d", p_rsFaceName.c_str(), p_iHeight);
	
	if(p_ePitch == PITCH_VARIABLE) 
	{
		s += "Variable ";
	}
	else
	{
		s += "Fixed ";
	}

	switch(p_eWeight) 
	{
		case W_THIN :		s += "thin ";			break;
		case W_EXTRALIGHT : s += "extralight ";		break;
		case W_ULTRALIGHT : s += "ultralight ";		break;
		case W_LIGHT :		s += "light ";			break;
		case W_NORMAL :		s += "normal ";			break;
		case W_REGULAR :	s += "regular ";		break;
		case W_MEDIUM :		s += "medium ";			break;
		case W_SEMIBOLD :	s += "semibold ";		break;
		case W_DEMIBOLD :	s += "demibold ";		break;
		case W_BOLD :		s += "bold ";			break;
		case W_EXTRABOLD :	s += "extrabold ";		break;
		case W_ULTRABOLD :	s += "ultrabold ";		break;
		case W_HEAVY :		s += "heavy ";			break;
		case W_BLACK :		s += "black ";			break;
		default:
			break;
	}

	if(p_iFlags & F_ITALIC)
	{
		s += "italic ";
	}
	if(p_iFlags & F_UNDERLINE)
	{
		s += "underline ";
	}
	if(p_iFlags & F_STRIKEOUT)
	{
		s += "strikeout ";
	}

	switch(p_eLocale) 
	{
		case L_JAPANESE :	s += "japanese ";	break;
		case L_KOREAN :		s += "korean ";		break;
		case L_CHINESE :	s += "chinese ";	break;
		case L_RUSSIAN :	s += "russian ";	break;
		case L_THAI :		s += "thai ";		break;
		case L_ARABIC :		s += "arabic ";		break;
		case L_HEBREW :		s += "hebrew ";		break;
		case L_VIETNAMESE :	s += "vietnamese ";	break;
		default:
			break;
	}
		
	return s;
}


bool CFont::operator== (const CFont& p_rxOther) const
{
	if(GetFontType() != p_rxOther.GetFontType()) 
	{
		return false;
	}

	return	m_xFontMetrics.m_iFlags == p_rxOther.m_xFontMetrics.m_iFlags   &&
			m_xFontMetrics.m_eWeight == p_rxOther.m_xFontMetrics.m_eWeight  &&
			m_xFontMetrics.m_ePitch == p_rxOther.m_xFontMetrics.m_ePitch &&
			m_eLocale == p_rxOther.m_eLocale  &&
			m_sFaceName == p_rxOther.m_sFaceName;
}



} // namespace UILib

