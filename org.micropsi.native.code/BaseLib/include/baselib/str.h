// string.h
//
// author: David.Salz@snafu.de
// created: November 4, 2003 


#ifndef STRING_H_INCLUDED
#define STRING_H_INCLUDED

#include <stdarg.h>

#include "baselib/dynarray.h"

class CStr
{
private:

	/// local class for real string data which can be shared by several string objects
	class StringData
	{
	public:
		StringData() : m_iRefCount(0), m_iCapacity(0), m_iLength(0), m_pc(0) {};

		int		m_iRefCount;		///< reference counter
		int		m_iCapacity;		///< capacity of buffer (including terminator)
		int		m_iLength;			///< length of string (excluding terminator)
		char*	m_pc;				///< string buffer 
	};

	StringData*	m_pxData;

	static StringData*	CreateStringData(int p_iCapacity);
	static void			DetachStringData(StringData*& p_pxrStringData);
	void				MakeUnshared();

public:


	CStr();
	CStr(const char* p_pCStr);
	CStr(const CStr& p_ksrOther);
	~CStr();

	static CStr	Create(const char* pcFormat,...);


	void		Clear();
	int			Compare(const char* p_pcOp) const;
	int			CompareNoCase(const char* p_pcOp) const;

	int			GetLength() const;
	bool		IsEmpty() const;

	int			Find(char p_cChar, int p_iStart = 0) const;
	int			FindAnyOf(const char* p_pcChars, int p_iStart = 0) const;
	int			Find(const char* p_pCStr, int p_iStart = 0) const;

	int			FindReverse(char p_cChar, int p_iStart = 2147483647) const;

	void		Format(const char* p_pcFormat,...);
	void		FormatV(const char* p_pcFormat,va_list arglist);
	
	char		GetAt(int p_iAt) const;

	CStr		Mid(int p_iFirst) const;
	CStr		Mid(int p_iFirst, int p_iCount ) const;
	CStr		Right(int p_iCount) const;
	CStr		Left(int p_iCount) const;

	void		MakeUpper();
	void		MakeLower();
	void		MakeReverse();

	CStr		ToUpper();
	CStr		ToLower();
	CStr		ToReverse();

	CStr&		Delete(int p_iFirst, int p_iCount = 1);

	CStr&		Insert(int p_iIndex, char p_cChar);
	CStr&		Insert(int p_iIndex, const char* p_pcString);
	CStr&		Insert(int p_iIndex, const CStr& p_krxString);

	CStr&		Replace(char p_cReplace, char p_cReplaceWith);
	CStr&		Replace(const char* p_pcStringOriginal, const char* p_pcStringSubstitute);
	CStr&		Remove(char p_cChar);

	CStr&		TrimRight(char p_cChar);
	CStr&		TrimLeft(char p_cChar);

	CStr&		SetAt(int p_iIndex, char p_cChar);

	void		Split(CDynArray<CStr>& po_rasArray, const char* p_pcSplitChars);

	int			ToInt() const;
	float		ToFloat() const;
	double		ToDouble() const;

	const char*	c_str() const;

	char		operator[](int p_iAt) const;

	CStr&		operator=(const CStr& p_ksrOther);

	bool		operator==(const CStr& p_ksrOther) const;
	bool		operator!=(const CStr& p_ksrOther) const;	
	bool		operator==(const char* p_pCStr) const;
	bool		operator!=(const char* p_pCStr) const;
	bool		operator<(const CStr& p_ksrOther) const;
	bool		operator>(const CStr& p_ksrOther) const;
	bool		operator<(const char* p_pCStr) const;
	bool		operator>(const char* p_pCStr) const;

	CStr&		operator+=(char p_cChar);
	CStr&		operator+=(const CStr& p_ksrOther);
	CStr		operator+ (const CStr& p_ksrOther) const;
};

#include "baselib/str.inl"

#endif	// STRING_H_INCLUDED

