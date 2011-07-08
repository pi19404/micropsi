#ifndef FOURCC_H_INCLUDED
#define FOURCC_H_INCLUDED

#include <string.h>
#include "baselib/str.h"
#include "baselib/macros.h"

class CFourCC 
{
protected: 
	 long		m_iValue;

public:

	CFourCC() 
	{
		m_iValue = 0;
	}


	CFourCC(long p_iInt)
	{
		m_iValue = p_iInt;
	}

	CFourCC(const char* p_kpcChar)
	{	
		m_iValue = 0;
		for (int i=min(3, (int) strlen(p_kpcChar)-1); i>=0; i--)
		{
			m_iValue <<= 8;
			m_iValue += (unsigned char)p_kpcChar[i];
		}
	}

	CFourCC(const CStr& p_ksrString)
	{		
		m_iValue = 0;
		for (int i=min(3, p_ksrString.GetLength()-1); i>=0; i--)
		{
			m_iValue <<= 8;
			m_iValue += (unsigned char) p_ksrString.GetAt(i);
		}
	}	
		
	~CFourCC() {}

	CFourCC& operator=(const CFourCC& p_krxFourCC)
	{
		m_iValue = p_krxFourCC.m_iValue;
		return *this;
	}

	
	/// Wert als String
	CStr AsString() const
	{		
		CStr sString = "NONE";
		
		sString.SetAt( 0, ( char ) ( m_iValue & 0x000000FF ));
		sString.SetAt( 1, ( char ) (( m_iValue & 0x0000FF00 ) >> 8) );
		sString.SetAt( 2, ( char ) (( m_iValue & 0x00FF0000 ) >> 16) );
		sString.SetAt( 3, ( char ) (( m_iValue & 0xFF000000 ) >> 24) );		
		return sString;
	}

	/// Wert als Integer (32 bit)
	long AsInt() const
	{
		return m_iValue;
	}

	/// Vergleichsoperator
	bool operator==(const CFourCC& p_krxOp) const
	{
		return m_iValue == p_krxOp.m_iValue;
	}

	/// Vergleichsoperator "ungleich"
	bool operator!=(const CFourCC& p_krxOp) const
	{
		return m_iValue != p_krxOp.m_iValue;
	}
	
	/// Operator "kleiner als"
	bool operator<(const CFourCC& p_krxOp) const
	{
		return m_iValue < p_krxOp.m_iValue;
	}

	/// Operator "größer als"
	bool operator>(const CFourCC& p_krxOp) const
	{
		return m_iValue > p_krxOp.m_iValue;
	}

};


#endif // ifndef FOURCC_H_INCLUDED


