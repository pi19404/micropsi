#pragma once

#ifndef BASELIB_COMOBJECTPTR_H_INCLUDED
#define BASELIB_COMOBJECTPTR_H_INCLUDED

#include <assert.h>

#ifndef NULL
#define NULL (0)
#endif

template<typename Type>
class CComObjectPtr
{
public:
	
	CComObjectPtr();
	CComObjectPtr(const CComObjectPtr<Type>& p_sprxOther);
	CComObjectPtr(Type* p_pxOther);

	~CComObjectPtr();


	CComObjectPtr<Type>& operator=(const CComObjectPtr<Type>& p_sprxOther);
	CComObjectPtr<Type>& operator=(Type* p_pxOther);

	bool				 operator==(const CComObjectPtr<Type>& p_sprxOther) const;
	bool				 operator!=(const CComObjectPtr<Type>& p_sprxOther) const;

	bool				 operator==(Type* p_pxOther) const;
	bool				 operator!=(Type* p_pxOther) const;

	bool				 IsNull() const;

	Type*				 operator->() const;
	Type&				 operator*();

						 operator Type*() const;
    Type**               operator&();       ///< dient dazu, dass man CreateXYZ(..., &pd3dDevice) aufrufen kann (pointer muss dazu 0 sein), AddRef wird implizit von der CreateFunktion aufgerufen
    const Type**         operator&() const; 

private:
	Type*	m_pxComObject;					///< pointer auf das Objekt

};

#include "baselib/comobjectptr.inl"

#endif // BASELIB_COMOBJECTPTR_H_INCLUDED