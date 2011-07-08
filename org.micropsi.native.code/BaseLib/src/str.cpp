// string.cpp
//
// author: David.Salz@snafu.de
// created: November 4, 2003 

#include "stdafx.h"

#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <assert.h>

#include "baselib/str.h"
#include "baselib/macros.h"


//---------------------------------------------------------------------------------------------------------------------
/**
	constructor
*/
CStr::CStr()
{
	m_pxData = 0;
}



//---------------------------------------------------------------------------------------------------------------------
/**
	constructor
*/
CStr::CStr(const char* p_pCStr)
{
	int iLength = (int) strlen(p_pCStr);
	if(iLength > 0)
	{	
		m_pxData = CreateStringData(iLength+2);
		m_pxData->m_iLength = iLength;
		strncpy(m_pxData->m_pc, p_pCStr, m_pxData->m_iCapacity);
	}
	else
	{
		m_pxData = 0;
	}
}



//---------------------------------------------------------------------------------------------------------------------
/**
	copy constructor
*/
CStr::CStr(const CStr& p_ksrOther)
{
	m_pxData = p_ksrOther.m_pxData;
	if(m_pxData)
	{
		m_pxData->m_iRefCount++;
	}
}



//---------------------------------------------------------------------------------------------------------------------
/**
	destructor
*/
CStr::~CStr()
{
	DetachStringData(m_pxData);
}



//---------------------------------------------------------------------------------------------------------------------
/**
	create a new stringdata and attach it to this string
*/
CStr::StringData* CStr::CreateStringData(int p_iCapacity)
{
	if(p_iCapacity == 0)
	{
		return 0;
	}

	CStr::StringData* p = new StringData();
	p->m_iRefCount++;
	p->m_iCapacity = p_iCapacity;
	p->m_pc = new char[p_iCapacity];
	return p;
}



//---------------------------------------------------------------------------------------------------------------------
/**
	detaches this string form its stringdata --> string data pointer is 0 after this call
	string data is deleted if it is not used any more
*/
void 
CStr::DetachStringData(StringData*& p_pxrStringData)
{
	if(!p_pxrStringData)	{ return; }
	
	p_pxrStringData->m_iRefCount--;
	if(p_pxrStringData->m_iRefCount <= 0)
	{
		if(p_pxrStringData->m_pc)
		{
			delete [] p_pxrStringData->m_pc;
		}
		delete p_pxrStringData;
	}
	p_pxrStringData = 0;
}



//---------------------------------------------------------------------------------------------------------------------
/**
	makes sure my string data is not shared
*/
void 
CStr::MakeUnshared()
{
	if(!m_pxData)	{ return; }
	if(m_pxData->m_iRefCount > 1)
	{
		m_pxData->m_iRefCount--;
		StringData* pxNewStrData = CreateStringData(m_pxData->m_iCapacity);
		strncpy(pxNewStrData->m_pc, m_pxData->m_pc, m_pxData->m_iCapacity);
		pxNewStrData->m_iLength = m_pxData->m_iLength;
		m_pxData = pxNewStrData;
	}
}



//---------------------------------------------------------------------------------------------------------------------
/**	
	compares two strings
	\return		0: equal; <0: this string is less than the argument; >0: this string is greater than the argument 
*/
int 
CStr::Compare(const char* p_pcOp) const
{
	if(!m_pxData)
	{
		return p_pcOp != 0;
	}
	return strcmp(m_pxData->m_pc, p_pcOp);
}



//---------------------------------------------------------------------------------------------------------------------
/**	
	compares two strings
	\return		0: equal; <0: this string is less than the argument; >0: this string is greater than the argument 
*/
int 
CStr::CompareNoCase(const char* p_pcOp) const
{
	if(!m_pxData)
	{
		return p_pcOp != 0;
	}
	return stricmp(m_pxData->m_pc, p_pcOp);
}



//---------------------------------------------------------------------------------------------------------------------
/**
	\return index of first occurence of character p_cChar in this string (first char = index 0); -1 if not found
*/
int	
CStr::Find(char p_cChar, int p_iStart ) const
{
	if(m_pxData  &&  m_pxData->m_pc)
	{
		int i;
		for(i=p_iStart; i<m_pxData->m_iLength; ++i)
		{
			if(m_pxData->m_pc[i] == p_cChar)
			{
				return i;
			}
		}
	}

	return -1;
}

//---------------------------------------------------------------------------------------------------------------------
int	
CStr::FindAnyOf(const char* p_pcChars, int p_iStart) const
{
	if(m_pxData  &&  m_pxData->m_pc)
	{
		CStr s = p_pcChars;

		int i;
		for(i=p_iStart; i<m_pxData->m_iLength; ++i)
		{
			if(s.Find(m_pxData->m_pc[i]) >= 0)
			{
				return i;
			}
		}
	}

	return -1;
}

//---------------------------------------------------------------------------------------------------------------------
/**
	\return index of first occurence of substring p_kpCStr in this string (first char = index 0); -1 if not found
*/
int 
CStr::Find(const char* p_kpCStr, int p_iStart ) const
{
	if(!m_pxData || m_pxData->m_pc == 0)
	{
		return -1;
	}

	int iTargetLength = (int) strlen(p_kpCStr);
	if(iTargetLength == 0)
	{
		return -1;
	}

	int i;
	for(i=p_iStart; i<m_pxData->m_iLength-iTargetLength+1; ++i)
	{
		int j;
		for(j=0; j<iTargetLength; ++j)
		{
			if(m_pxData->m_pc[i+j] != p_kpCStr[j])
			{
				break;
			};
		};
		if(j==iTargetLength)
		{
			return i;
		}
	}

	return -1;
}


//---------------------------------------------------------------------------------------------------------------------
int			
CStr::FindReverse(char p_cChar, int p_iStart) const
{
	if(m_pxData  &&  m_pxData->m_pc)
	{
		p_iStart = min(p_iStart, GetLength()-1);
		int i;
		for(i=p_iStart; i>=0; --i)
		{
			if(m_pxData->m_pc[i] == p_cChar)
			{
				return i;
			}
		}
	}

	return -1;
}


//---------------------------------------------------------------------------------------------------------------------
/**
	Format function; works like a printf into this string
*/
void 
CStr::Format(const char* p_pcFormat,...)
{
	va_list argList;
	va_start(argList, p_pcFormat);
	FormatV(p_pcFormat, argList);
	va_end(argList);
}


//---------------------------------------------------------------------------------------------------------------------
/**
	Format function; works like a printf into this string
*/
void 
CStr::FormatV(const char* p_pcFormat,va_list arglist)
{
	DetachStringData(m_pxData);

	char cB[4096];
	int iLen = _vsnprintf(cB,4096,p_pcFormat,arglist);
	if(iLen > 0)
	{
		m_pxData = CreateStringData(iLen+1);
		m_pxData->m_iLength = iLen;
		strncpy(m_pxData->m_pc, cB, m_pxData->m_iCapacity);
	}
}


//---------------------------------------------------------------------------------------------------------------------
CStr 
CStr::Create(const char* p_pcFormat,...)
{
	va_list argList;
	va_start(argList, p_pcFormat);
	char cB[4096];
	int iLen=_vsnprintf(cB,4096,p_pcFormat,argList);
	va_end(argList);
	return CStr(cB);
}


//---------------------------------------------------------------------------------------------------------------------
/**
	returns a substring from the middle of this string to the end

	\param p_iFirst		index of character to start at (first = 0)
	\return				a substring; may be empty
*/
CStr	
CStr::Mid(int p_iFirst) const
{
	if(p_iFirst < 0)	{ p_iFirst = 0; }
	if(!m_pxData || m_pxData->m_pc==0 || p_iFirst >= m_pxData->m_iLength)
	{
		return CStr();
	}
	else
	{
		return CStr(&(m_pxData->m_pc[p_iFirst]));
	}
}


//---------------------------------------------------------------------------------------------------------------------
/**
	returns a substring from the middle of this string

	\param p_iFirst		index of character to start at (first = 0)
	\param p_iCount		number of characters to copy; substring may be shorter than that, if source string is not long enough 
	\return				a substring; may be empty
*/
CStr	
CStr::Mid(int p_iFirst, int p_iCount ) const
{
	if(p_iFirst < 0)	{ p_iFirst = 0; }
	if(!m_pxData || m_pxData->m_pc==0 || p_iFirst >= m_pxData->m_iLength)
	{
		return CStr();
	}
	else if(p_iFirst + p_iCount > m_pxData->m_iLength)
	{
		return CStr(&(m_pxData->m_pc[p_iFirst]));
	}
	else
	{
		char c = m_pxData->m_pc[p_iFirst + p_iCount];
		m_pxData->m_pc[p_iFirst + p_iCount] = 0;
		CStr s = &(m_pxData->m_pc[p_iFirst]);
		m_pxData->m_pc[p_iFirst + p_iCount] = c;
		return s;
	}
}


//---------------------------------------------------------------------------------------------------------------------
CStr 
CStr::Right(int p_iCount) const
{
	if(p_iCount < GetLength())
	{
		return Mid(GetLength() - p_iCount, p_iCount);		
	}
	else
	{
		return *this;
	}
}



//---------------------------------------------------------------------------------------------------------------------
CStr 
CStr::Left(int p_iCount) const
{
	return Mid(0, p_iCount);
}


//---------------------------------------------------------------------------------------------------------------------
void		
CStr::MakeUpper()
{
	if(!m_pxData) { return; }
	MakeUnshared();
	strupr(m_pxData->m_pc);
}


//---------------------------------------------------------------------------------------------------------------------
void		
CStr::MakeLower()
{
	if(!m_pxData) { return; }
	MakeUnshared();
	strlwr(m_pxData->m_pc);
}


//---------------------------------------------------------------------------------------------------------------------
void		
CStr::MakeReverse()
{
	if(!m_pxData) { return; }
	MakeUnshared();
	strrev(m_pxData->m_pc);
}


//---------------------------------------------------------------------------------------------------------------------
CStr		
CStr::ToUpper()
{
	CStr s(*this);
	s.MakeUpper();
	return s;
}


//---------------------------------------------------------------------------------------------------------------------
CStr		
CStr::ToLower()
{
	CStr s(*this);
	s.MakeLower();
	return s;
}


//---------------------------------------------------------------------------------------------------------------------
CStr		
CStr::ToReverse()
{
	CStr s(*this);
	s.MakeReverse();
	return s;
}


//---------------------------------------------------------------------------------------------------------------------
CStr& 
CStr::Delete(int p_iFirst, int p_iCount)
{
	int iLength = GetLength();
	if(iLength == 0)	 { return *this; }

	p_iFirst = clamp(p_iFirst, 0, iLength-1);
	p_iCount = clamp(p_iCount, 0, iLength - p_iFirst);
	
	if(p_iFirst == 0  &&  p_iCount >= iLength)
	{
		Clear();
		return *this;
	}

	StringData* pxNewData = CreateStringData(iLength-p_iCount+1);
	if(p_iFirst > 0)
	{
		strncpy(pxNewData->m_pc, m_pxData->m_pc, p_iFirst);
	}
	if(p_iFirst + p_iCount < iLength)
	{
		strncpy(pxNewData->m_pc + p_iFirst, m_pxData->m_pc + (p_iFirst + p_iCount), iLength - (p_iFirst + p_iCount));
	}
	pxNewData->m_pc[iLength-p_iCount] = 0;

	pxNewData->m_iLength = m_pxData->m_iLength-p_iCount;
	DetachStringData(m_pxData);
	m_pxData = pxNewData;		

	return *this;	
}


//---------------------------------------------------------------------------------------------------------------------
CStr& 
CStr::Insert(int p_iIndex, char p_cChar)
{
	int iLength = GetLength();
	if(p_iIndex < 0  ||  p_iIndex > iLength)
	{
		return *this;
	}

	StringData* pxNewData = CreateStringData(iLength + 2);
	if(p_iIndex > 0)
	{
		strncpy(pxNewData->m_pc, m_pxData->m_pc, p_iIndex);
	}
	pxNewData->m_pc[p_iIndex] = p_cChar;
	if(p_iIndex < iLength-1)
	{
		strncpy(pxNewData->m_pc + p_iIndex + 1, m_pxData->m_pc + p_iIndex, iLength - p_iIndex);
	}
	pxNewData->m_pc[iLength+1] = 0;

	pxNewData->m_iLength = iLength+1;
	DetachStringData(m_pxData);
	m_pxData = pxNewData;	

	return *this;	
}


//---------------------------------------------------------------------------------------------------------------------
CStr& 
CStr::Insert(int p_iIndex, const char* p_pcString)
{
	int iLength = GetLength();
	if(p_iIndex < 0  ||  p_iIndex > iLength)	{ return *this; }
	int iInsertLength = (int) strlen(p_pcString);
	if(iInsertLength < 1)	{ return *this; }

	StringData* pxNewData = CreateStringData(iLength + 1 + iInsertLength);
	if(p_iIndex > 0)
	{
		strncpy(pxNewData->m_pc, m_pxData->m_pc, p_iIndex);
	}
	strncpy(pxNewData->m_pc + p_iIndex, p_pcString, iInsertLength);
	if(p_iIndex < iLength-1)
	{
		strncpy(pxNewData->m_pc + p_iIndex + iInsertLength, m_pxData->m_pc + p_iIndex, iLength - p_iIndex);
	}
	pxNewData->m_pc[iLength+iInsertLength] = 0;

	pxNewData->m_iLength = iLength+iInsertLength;
	DetachStringData(m_pxData);
	m_pxData = pxNewData;	

	return *this;	
}


//---------------------------------------------------------------------------------------------------------------------
CStr& 
CStr::Replace(char p_cReplace, char p_cReplaceWith)
{
	MakeUnshared();
	for(int i=0; i<GetLength(); ++i)
	{
		if(m_pxData->m_pc[i] == p_cReplace)
		{
			m_pxData->m_pc[i] = p_cReplaceWith;
		}
	}
	return *this;
}

//---------------------------------------------------------------------------------------------------------------------
CStr&		
CStr::Replace(const char* p_pcStringOriginal, const char* p_pcStringSubstitute)
{
	int iOriginalLen = (int) strlen(p_pcStringOriginal);
	int i = Find(p_pcStringOriginal);
	while(i >= 0)
	{
		Delete(i, iOriginalLen);
		Insert(i, p_pcStringSubstitute);
		i = Find(p_pcStringOriginal);
	}
	return *this;
}

//---------------------------------------------------------------------------------------------------------------------
CStr&	
CStr::Remove(char p_cChar)
{
	int iFound = 0;
	for(int i=0; i<GetLength(); ++i)
	{
		if(m_pxData->m_pc[i] == p_cChar)
		{
			iFound++;
		}
	}

	if(iFound == 0) { return *this; }
	if(iFound == GetLength()) 
	{
		Clear();
		return *this;
	}

	int iNewLength = GetLength() - iFound;
	StringData* pxNewData = CreateStringData(iNewLength + 1);
	char* p = pxNewData->m_pc;
	for(int i=0; i<GetLength(); ++i)
	{
		if(m_pxData->m_pc[i] != p_cChar)
		{
			*p = m_pxData->m_pc[i];
			p++;
		}
	}
	*p = 0;

	pxNewData->m_iLength = iNewLength;
	DetachStringData(m_pxData);
	m_pxData = pxNewData;		

	return *this;
}


//---------------------------------------------------------------------------------------------------------------------
CStr&		
CStr::TrimRight(char p_cChar)
{
	int iLen = GetLength();
	int i = iLen-1;
	while(i >= 0  &&  m_pxData->m_pc[i] == p_cChar)
	{
		--i;
	}

	if(i == iLen-1)
	{
		return *this;
	}
	else if(i < 0)
	{
		Clear();
		return *this;
	}
	else
	{
		*this = Left(i+1);
		return *this;
	}
}


//---------------------------------------------------------------------------------------------------------------------
CStr&		
CStr::TrimLeft(char p_cChar)
{
	int iLen = GetLength();
	int i = 0;
	while(i < iLen  &&  m_pxData->m_pc[i] == p_cChar)
	{
		++i;
	}

	if(i == 0)
	{
		return *this;
	}
	else if(i == iLen)
	{
		Clear();
		return *this;
	}
	else
	{
		*this = Mid(i, iLen-i);
		return *this;
	}
}


//---------------------------------------------------------------------------------------------------------------------
CStr& 
CStr::SetAt(int p_iIndex, char p_cChar)
{
	if(p_iIndex < 0  ||  p_iIndex >= GetLength())	{ return *this; }
	MakeUnshared();
	m_pxData->m_pc[p_iIndex] = p_cChar;
	return *this;
}


//---------------------------------------------------------------------------------------------------------------------
void		
CStr::Split(CDynArray<CStr>& po_rasArray, const char* p_pcSplitChars)
{
	int iLen = GetLength();
	int iStart = 0;
	CStr sSplitChars = p_pcSplitChars;

	while(iStart < iLen)
	{
		int iEnd = FindAnyOf(p_pcSplitChars, iStart);
		if(iEnd == -1)
		{
			iEnd = GetLength();
		}
		po_rasArray.PushEntry(Mid(iStart, iEnd - iStart));
		iStart = iEnd +1;
		while(iStart < iLen  &&  sSplitChars.Find(m_pxData->m_pc[iStart]) >= 0)
		{
			iStart++;
		}
	}
}


//---------------------------------------------------------------------------------------------------------------------
/**
	\return		an integer representation of this string (or nonsense if this string is not a valid int)
*/
int	
CStr::ToInt() const
{
	if(m_pxData  &&  m_pxData->m_pc)
	{
		return atoi(m_pxData->m_pc);
	}
	else
	{
		return 0;
	}
}


//---------------------------------------------------------------------------------------------------------------------
float		
CStr::ToFloat() const
{
	if(m_pxData  &&  m_pxData->m_pc)
	{
		return (float) atof(m_pxData->m_pc);
	}
	else
	{
		return 0;
	}
}


//---------------------------------------------------------------------------------------------------------------------
double		
CStr::ToDouble() const
{
	if(m_pxData  &&  m_pxData->m_pc)
	{
		return atof(m_pxData->m_pc);
	}
	else
	{
		return 0;
	}
}


//---------------------------------------------------------------------------------------------------------------------
CStr& 
CStr::operator=(const CStr& p_srOther)
{
	DetachStringData(m_pxData);
	m_pxData = p_srOther.m_pxData;
	if(m_pxData)
	{
		m_pxData->m_iRefCount++;
	}
	return *this;
}


//---------------------------------------------------------------------------------------------------------------------
/**	
	comparison operator
*/
bool 
CStr::operator==(const CStr& p_ksrOther) const	
{ 
	if(p_ksrOther.m_pxData == m_pxData)
	{
		return true;
	}
	else if(p_ksrOther.m_pxData == 0 || m_pxData == 0)
	{
		return false;
	}
	else
	{
		return Compare(p_ksrOther.m_pxData->m_pc) == 0; 
	}
}


//---------------------------------------------------------------------------------------------------------------------
/**	
	comparison operator
*/
bool 
CStr::operator!=(const CStr& p_ksrOther) const	
{ 
	if(p_ksrOther.m_pxData == m_pxData)
	{
		return false;
	}
	else if(p_ksrOther.m_pxData == 0 || m_pxData == 0)
	{
		return true;
	}
	else
	{
		return Compare(p_ksrOther.m_pxData->m_pc) != 0; 
	}
}


//---------------------------------------------------------------------------------------------------------------------
CStr& 
CStr::operator+=(char p_cChar)
{
	int iOldLength = GetLength();
	StringData* pxNewData = CreateStringData(iOldLength+2);
	if(iOldLength > 0)
	{
		strncpy(pxNewData->m_pc, m_pxData->m_pc, iOldLength);
	}
	pxNewData->m_pc[iOldLength] = p_cChar;
	pxNewData->m_pc[iOldLength+1] = 0;
	pxNewData->m_iLength = iOldLength+1;
	DetachStringData(m_pxData);
	m_pxData = pxNewData;		

	return *this;
}


//---------------------------------------------------------------------------------------------------------------------
CStr& 
CStr::operator+=(const CStr& p_ksrOther)
{
	int iOtherLength = p_ksrOther.GetLength();
	if(iOtherLength == 0)
	{
		return *this;
	}

	int iOldLength = GetLength();
	StringData* pxNewData = CreateStringData(iOldLength + iOtherLength +1);
	if(pxNewData == 0)
	{
		DetachStringData(m_pxData);
		return *this;
	}

	if(iOldLength > 0)
	{
		strncpy(pxNewData->m_pc, m_pxData->m_pc, iOldLength);
	}
	strncpy(pxNewData->m_pc+iOldLength, p_ksrOther.m_pxData->m_pc, iOtherLength);
	pxNewData->m_pc[iOldLength+iOtherLength] = 0;
	pxNewData->m_iLength = iOldLength+iOtherLength;

	DetachStringData(m_pxData);
	m_pxData = pxNewData;		

	return *this;
}


//---------------------------------------------------------------------------------------------------------------------
/**
	operator + : joins two strings
*/
CStr 
CStr::operator+ (const CStr& p_ksrOther) const
{
	CStr s(*this);
	s += p_ksrOther;
	return s;
}

//---------------------------------------------------------------------------------------------------------------------
