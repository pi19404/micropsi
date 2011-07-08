#ifndef BASELIB_VARIANT_H_INCLUDED
#define BASELIB_VARIANT_H_INCLUDED

#include <assert.h>
#include <vector>
#include <string>
#include "baselib/VariantType.h"


class CVariant
{
private:

	union
	{
		int i;
		float f;
		bool b;
		void* p;
	} m_xValue;

	CVariantType*	m_pxType;


	void* GetValuePointer();
	const void* GetValuePointer() const;
	void Allocate();
	void Deallocate();


public:

	const CVariantType* GetDesc() const;
	CVariant(CVariantType* pxType);
	~CVariant();
	CVariant(const CVariant& rxSource);

	
	/// Umwandlung in einen String und Lesen aus einem String 
	/// (Rückgabewert: false, wenn Funktion von dem Datentyp nicht unterstützt wird)
	bool SaveToString(std::string* psStringOut = NULL) const;
	bool LoadFromString(const std::string* psString = NULL);


	/// Wertzuweisung (ohne Typwechsel)
	CVariant& operator=(const CVariant& rxSource);

	/// Zuweisung mit Typwechsel
	void ReAssign(const CVariant& rxSource);


	/// Cast-Operator mit automatic type discovery (zum Auslesen)
	template<typename T>
	operator T();

	/// Zuweisungs-Operator mit automatic type discovery (zum Schreiben)
	template<typename T> 
	CVariant& operator=(const T& rxSource);
};
//---------------------------------------------------------------------------------------------------------------------

#include "Variant.inl"

#endif // BASELIB_VARIANT_H_INCLUDED
