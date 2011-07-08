#pragma once

#ifndef BASELIB_FUNCTIONPOINTER_H_INCLUDED
#define BASELIB_FUNCTIONPOINTER_H_INCLUDED

/*
	Die folgenden Klassen basieren auf dem Artikel 

	"CALLBACKS IN C++ USING TEMPLATE FUNCTORS" 
	von Rich Hickey (1994)
	http://www.tutok.sk/fastgl/callback.html

	zur Verwendung siehe auch das Testprogamm in BaseLibTest
*/


//-------------------------------------------------------------------------------------------------------------------------------------------
/**
	Basisklasse für einen FunctionPointer

	enthält bereits alle Datenmember, d.h. Pointer auf Objekt und Pointer auf Funktion
	enthält diese Daten in TYPLOSER Form, da der Besitzer des Pointers zur Kompilezeit nicht weiss,
	wohin der Pointer zur Laufzeit zeigen wird
*/
class CFunctionPointerBase
{
public:
	typedef void (CFunctionPointerBase::*_MemFunc)();	///< generische Definition eines Memberfunction-Pointers
	typedef void (*_Func)();							///< generische Definition eines (statischen) Function-Pointers
	
	union {
		const void *m_fpStaticFunction;					///< statischer Functionpointer
		char m_fpMemberFunction[sizeof(_MemFunc)];		///< Memberfunctionpointer - muss als char[] gespeichert werden, void* geht nicht
	};

	void* m_pxObject;									///< Pointer auf Objekt, auf dem Memberfunktion gerufen wird (kann 0 sein)

	/// default - ctor
	CFunctionPointerBase() :  m_pxObject(0) {}

	/// ctor
	CFunctionPointerBase(void* pxObject, void* fpFunction, int iFunctionPointerSize) :  
		m_pxObject(pxObject)
	{
		if(pxObject)
		{
			memcpy(m_fpMemberFunction, fpFunction, iFunctionPointerSize);
		}
		else
		{
			m_fpStaticFunction = fpFunction;
		}
	}
};


//-------------------------------------------------------------------------------------------------------------------------------------------
// 0 arguments, no return value
//-------------------------------------------------------------------------------------------------------------------------------------------

/// Function Pointer ohne Argumente und ohne Rückgabewert
class CFunctionPointer0 : public CFunctionPointerBase
{
public:
    typedef void (__cdecl* TTranslatorFunctionPointer)(CFunctionPointerBase* pCallbackData);
    TTranslatorFunctionPointer m_fpTranslatorFunction;

	CFunctionPointer0() : m_fpTranslatorFunction(0) {}

	CFunctionPointer0(void* pxObject, void* fpFunction, int iFunctionPointerSize, TTranslatorFunctionPointer pxTranslatorFunction) : 
		CFunctionPointerBase(pxObject, fpFunction, iFunctionPointerSize),
		m_fpTranslatorFunction(pxTranslatorFunction) {}

	operator bool()
	{
		return m_fpTranslatorFunction != 0;
	}

	void operator()()
	{
		m_fpTranslatorFunction(this);
	}
};

/// Function Pointer ohne Argumente und ohne Rückgabewert; Translator für Member-Funktionen
template<typename TObject, typename TFunction>
class CFunctionPointer0MemberFunctionTranslator : public CFunctionPointer0
{
public:
	CFunctionPointer0MemberFunctionTranslator(TObject* pxObject, const TFunction& function) : 
	  CFunctionPointer0(pxObject, (void*) &function, sizeof(function), Call) {}

    static void Call(CFunctionPointerBase* pCallbackData)
	{
		TObject* pObject = (TObject*) pCallbackData->m_pxObject;
		TFunction &memFunc(*(TFunction*)(void *)(pCallbackData->m_fpMemberFunction));
		(pObject->*memFunc)();
	}
};

/// Function Pointer ohne Argumente und ohne Rückgabewert; Translator für globale (d.h. statische) Funktionen
template<typename TFunction>
class CFunctionPointer0StaticFunctionTranslator : public CFunctionPointer0
{
public:
	CFunctionPointer0StaticFunctionTranslator(const TFunction& function) : 
	  CFunctionPointer0(0, function, 0, Call) {}

    static void Call(CFunctionPointerBase* pCallbackData)
	{
		(TFunction(pCallbackData->m_fpStaticFunction))();
	}
};

/// Create-Funktion: Member-Funktion: keine Argumente, kein Rückgabewert
template<typename TObject>
inline
CFunctionPointer0MemberFunctionTranslator<TObject, void (TObject::*)()>
CreateFunctionPointer0(TObject* pxObject, void (TObject::*fpFunction)())
{
    return CFunctionPointer0MemberFunctionTranslator<TObject, void (TObject::*)()>(pxObject, fpFunction);
}

/// Create-Funktion: statische Funktion: keine Argumente, kein Rückgabewert
inline
CFunctionPointer0StaticFunctionTranslator<void (*)()>
CreateFunctionPointer0(void (*fpFunction)())
{
    return CFunctionPointer0StaticFunctionTranslator<void (*)()>(fpFunction);
}


//-------------------------------------------------------------------------------------------------------------------------------------------
// 0 arguments, with return value
//-------------------------------------------------------------------------------------------------------------------------------------------

/// Function Pointer keine Argumente und mit Rückgabewert
template<typename TReturn>
class CFunctionPointer0R : public CFunctionPointerBase
{
public:
    typedef TReturn (__cdecl* TTranslatorFunctionPointer)(CFunctionPointerBase* pCallbackData);
    TTranslatorFunctionPointer m_fpTranslatorFunction;

	CFunctionPointer0R() : m_fpTranslatorFunction(0)  {}

	CFunctionPointer0R(void* pxObject, void* fpFunction, int iFunctionPointerSize, TTranslatorFunctionPointer pxTranslatorFunction) : 
		CFunctionPointerBase(pxObject, fpFunction, iFunctionPointerSize),
		m_fpTranslatorFunction(pxTranslatorFunction) {}

	operator bool()
	{
		return m_fpTranslatorFunction != 0;
	}

	TReturn operator()()
	{
		return m_fpTranslatorFunction(this);
	}
};

/// Function Pointer ohne Argumente und mit Rückgabewert; Translator für Member-Funktionen
template<typename TReturn, typename TObject, typename TFunction>
class CFunctionPointer0RTranslator : public CFunctionPointer0R<TReturn>
{
public:
	CFunctionPointer0RTranslator(TObject* pxObject, const TFunction& function) : 
	  CFunctionPointer0R<TReturn>(pxObject, (void*) &function, sizeof(function), Call) {}

    static TReturn Call(CFunctionPointerBase* pCallbackData)
	{
		TObject* pObject = (TObject*) pCallbackData->m_pxObject;
		TFunction &memFunc(*(TFunction*)(void *)(pCallbackData->m_fpMemberFunction));
		return (pObject->*memFunc)();
	}
};

/// Function Pointer mit einem Argument und ohne Rückgabewert; Translator für globale (d.h. statische) Funktionen
template<typename TReturn, typename TFunction>
class CFunctionPointer0RStaticFunctionTranslator : public CFunctionPointer0R<TReturn>
{
public:
	CFunctionPointer0RStaticFunctionTranslator(const TFunction& function) : 
	  CFunctionPointer0R<TReturn>(0, function, 0, Call) {}

    static TReturn Call(CFunctionPointerBase* pCallbackData)
	{
		return (TFunction(pCallbackData->m_fpStaticFunction))();
	}
};

/// Create-Funktion: Member-Funktion: ein Argument, kein Rückgabewert
template<typename TObject, typename TReturn>
inline
CFunctionPointer0RTranslator<TReturn, TObject, TReturn (TObject::*)()>
CreateFunctionPointer0R(TObject* pxObject, TReturn (TObject::*fpFunction)())
{
    return CFunctionPointer0RTranslator<TReturn, TObject, TReturn (TObject::*)()>(pxObject, fpFunction);
}

/// Create-Funktion: statische Funktion: ein Argument, kein Rückgabewert
template<typename TReturn>
inline
CFunctionPointer0RStaticFunctionTranslator<TReturn, TReturn (*)()>
CreateFunctionPointer0R(TReturn (*fpFunction)())
{
    return CFunctionPointer0RStaticFunctionTranslator<TReturn, TReturn (*)()>(fpFunction);
}



//-------------------------------------------------------------------------------------------------------------------------------------------
// 1 argument, no return value
//-------------------------------------------------------------------------------------------------------------------------------------------

/// Function Pointer ein Argument und ohne Rückgabewert
template<typename TArg0>
class CFunctionPointer1 : public CFunctionPointerBase
{
public:
    typedef void (__cdecl* TTranslatorFunctionPointer)(CFunctionPointerBase* pCallbackData, TArg0 arg0);
    TTranslatorFunctionPointer m_fpTranslatorFunction;

	CFunctionPointer1() : m_fpTranslatorFunction(0)  {}

	CFunctionPointer1(void* pxObject, void* fpFunction, int iFunctionPointerSize, TTranslatorFunctionPointer pxTranslatorFunction) : 
		CFunctionPointerBase(pxObject, fpFunction, iFunctionPointerSize),
		m_fpTranslatorFunction(pxTranslatorFunction) {}

	operator bool()
	{
		return m_fpTranslatorFunction != 0;
	}

	void operator()(TArg0 arg0)
	{
		m_fpTranslatorFunction(this, arg0);
	}
};

/// Function Pointer mit einem Argument und ohne Rückgabewert; Translator für Member-Funktionen
template<typename TArg0, typename TObject, typename TFunction>
class CFunctionPointer1Translator : public CFunctionPointer1<TArg0>
{
public:
	CFunctionPointer1Translator(TObject* pxObject, const TFunction& function) : 
	  CFunctionPointer1<TArg0>(pxObject, (void*) &function, sizeof(function), Call) {}

    static void Call(CFunctionPointerBase* pCallbackData, TArg0 arg0)
	{
		TObject* pObject = (TObject*) pCallbackData->m_pxObject;
		TFunction &memFunc(*(TFunction*)(void *)(pCallbackData->m_fpMemberFunction));
		(pObject->*memFunc)(arg0);
	}
};

/// Function Pointer mit einem Argument und ohne Rückgabewert; Translator für globale (d.h. statische) Funktionen
template<typename TArg0, typename TFunction>
class CFunctionPointer1StaticFunctionTranslator : public CFunctionPointer1<TArg0>
{
public:
	CFunctionPointer1StaticFunctionTranslator(const TFunction& function) : 
	  CFunctionPointer1<TArg0>(0, function, 0, Call) {}

    static void Call(CFunctionPointerBase* pCallbackData, TArg0 arg0)
	{
		(TFunction(pCallbackData->m_fpStaticFunction))(arg0);
	}
};

/// Create-Funktion: Member-Funktion: ein Argument, kein Rückgabewert
template<typename TObject, typename TArg0>
inline
CFunctionPointer1Translator<TArg0, TObject, void (TObject::*)(TArg0)>
CreateFunctionPointer1(TObject* pxObject, void (TObject::*fpFunction)(TArg0))
{
    return CFunctionPointer1Translator<TArg0, TObject, void (TObject::*)(TArg0)>(pxObject, fpFunction);
}

/// Create-Funktion: statische Funktion: ein Argument, kein Rückgabewert
template<typename TArg0>
inline
CFunctionPointer1StaticFunctionTranslator<TArg0, void (*)(TArg0)>
CreateFunctionPointer1(void (*fpFunction)(TArg0))
{
    return CFunctionPointer1StaticFunctionTranslator<TArg0, void (*)(TArg0)>(fpFunction);
}

//-------------------------------------------------------------------------------------------------------------------------------------------
// 1 argument, with return value
//-------------------------------------------------------------------------------------------------------------------------------------------

/// Function Pointer ein Argument und ohne Rückgabewert
template<typename TReturn, typename TArg0>
class CFunctionPointer1R : public CFunctionPointerBase
{
public:
    typedef TReturn (__cdecl* TTranslatorFunctionPointer)(CFunctionPointerBase* pCallbackData, TArg0 arg0);
    TTranslatorFunctionPointer m_fpTranslatorFunction;

	CFunctionPointer1R() : m_fpTranslatorFunction(0)  {}

	CFunctionPointer1R(void* pxObject, void* fpFunction, int iFunctionPointerSize, TTranslatorFunctionPointer pxTranslatorFunction) : 
		CFunctionPointerBase(pxObject, fpFunction, iFunctionPointerSize),
		m_fpTranslatorFunction(pxTranslatorFunction) {}

	operator bool()
	{
		return m_fpTranslatorFunction != 0;
	}

	TReturn operator()(TArg0 arg0)
	{
		return m_fpTranslatorFunction(this, arg0);
	}
};

/// Function Pointer mit einem Argument und ohne Rückgabewert; Translator für Member-Funktionen
template<typename TReturn, typename TArg0, typename TObject, typename TFunction>
class CFunctionPointer1RTranslator : public CFunctionPointer1R<TReturn, TArg0>
{
public:
	CFunctionPointer1RTranslator(TObject* pxObject, const TFunction& function) : 
	  CFunctionPointer1R<TReturn, TArg0>(pxObject, (void*) &function, sizeof(function), Call) {}

    static TReturn Call(CFunctionPointerBase* pCallbackData, TArg0 arg0)
	{
		TObject* pObject = (TObject*) pCallbackData->m_pxObject;
		TFunction &memFunc(*(TFunction*)(void *)(pCallbackData->m_fpMemberFunction));
		return (pObject->*memFunc)(arg0);
	}
};

/// Function Pointer mit einem Argument und ohne Rückgabewert; Translator für globale (d.h. statische) Funktionen
template<typename TReturn, typename TArg0, typename TFunction>
class CFunctionPointer1RStaticFunctionTranslator : public CFunctionPointer1R<TReturn, TArg0>
{
public:
	CFunctionPointer1RStaticFunctionTranslator(const TFunction& function) : 
	  CFunctionPointer1R<TReturn, TArg0>(0, function, 0, Call) {}

    static TReturn Call(CFunctionPointerBase* pCallbackData, TArg0 arg0)
	{
		return (TFunction(pCallbackData->m_fpStaticFunction))(arg0);
	}
};

/// Create-Funktion: Member-Funktion: ein Argument, kein Rückgabewert
template<typename TObject, typename TReturn, typename TArg0>
inline
CFunctionPointer1RTranslator<TReturn, TArg0, TObject, TReturn (TObject::*)(TArg0)>
CreateFunctionPointer1R(TObject* pxObject, TReturn (TObject::*fpFunction)(TArg0))
{
    return CFunctionPointer1RTranslator<TReturn, TArg0, TObject, TReturn (TObject::*)(TArg0)>(pxObject, fpFunction);
}

/// Create-Funktion: statische Funktion: ein Argument, kein Rückgabewert
template<typename TReturn, typename TArg0>
inline
CFunctionPointer1RStaticFunctionTranslator<TReturn, TArg0, TReturn (*)(TArg0)>
CreateFunctionPointer1R(TReturn (*fpFunction)(TArg0))
{
    return CFunctionPointer1RStaticFunctionTranslator<TReturn, TArg0, TReturn (*)(TArg0)>(fpFunction);
}

//-------------------------------------------------------------------------------------------------------------------------------------------
// 2 argument, no return value
//-------------------------------------------------------------------------------------------------------------------------------------------

/// Function Pointer ein Argument und ohne Rückgabewert
template<typename TArg0, typename TArg1>
class CFunctionPointer2 : public CFunctionPointerBase
{
public:
    typedef void (__cdecl* TTranslatorFunctionPointer)(CFunctionPointerBase* pCallbackData, TArg0 arg0, TArg1 arg1);
    TTranslatorFunctionPointer m_fpTranslatorFunction;

	CFunctionPointer2() : m_fpTranslatorFunction(0)  {}

	CFunctionPointer2(void* pxObject, void* fpFunction, int iFunctionPointerSize, TTranslatorFunctionPointer pxTranslatorFunction) : 
		CFunctionPointerBase(pxObject, fpFunction, iFunctionPointerSize),
		m_fpTranslatorFunction(pxTranslatorFunction) {}

	operator bool()
	{
		return m_fpTranslatorFunction != 0;
	}

	void operator()(TArg0 arg0, TArg1 arg1)
	{
		m_fpTranslatorFunction(this, arg0, arg1);
	}
};

/// Function Pointer mit einem Argument und ohne Rückgabewert; Translator für Member-Funktionen
template<typename TArg0, typename TArg1, typename TObject, typename TFunction>
class CFunctionPointer2Translator : public CFunctionPointer2<TArg0, TArg1>
{
public:
	CFunctionPointer2Translator(TObject* pxObject, const TFunction& function) : 
	  CFunctionPointer2<TArg0, TArg1>(pxObject, (void*) &function, sizeof(function), Call) {}

    static void Call(CFunctionPointerBase* pCallbackData, TArg0 arg0, TArg1 arg1)
	{
		TObject* pObject = (TObject*) pCallbackData->m_pxObject;
		TFunction &memFunc(*(TFunction*)(void *)(pCallbackData->m_fpMemberFunction));
		(pObject->*memFunc)(arg0, arg1);
	}
};

/// Function Pointer mit einem Argument und ohne Rückgabewert; Translator für globale (d.h. statische) Funktionen
template<typename TArg0, typename TArg1, typename TFunction>
class CFunctionPointer2StaticFunctionTranslator : public CFunctionPointer2<TArg0, TArg1>
{
public:
	CFunctionPointer2StaticFunctionTranslator(const TFunction& function) : 
	  CFunctionPointer2<TArg0, TArg1>(0, function, 0, Call) {}

    static void Call(CFunctionPointerBase* pCallbackData, TArg0 arg0, TArg1 arg1)
	{
		(TFunction(pCallbackData->m_fpStaticFunction))(arg0, arg1);
	}
};

/// Create-Funktion: Member-Funktion: ein Argument, kein Rückgabewert
template<typename TObject, typename TArg0, typename TArg1>
inline
CFunctionPointer2Translator<TArg0, TArg1, TObject, void (TObject::*)(TArg0, TArg1)>
CreateFunctionPointer2(TObject* pxObject, void (TObject::*fpFunction)(TArg0, TArg1))
{
    return CFunctionPointer2Translator<TArg0, TArg1, TObject, void (TObject::*)(TArg0, TArg1)>(pxObject, fpFunction);
}

/// Create-Funktion: statische Funktion: ein Argument, kein Rückgabewert
template<typename TArg0, typename TArg1>
inline
CFunctionPointer2StaticFunctionTranslator<TArg0, TArg1, void (*)(TArg0, TArg1)>
CreateFunctionPointer2(void (*fpFunction)(TArg0, TArg1))
{
    return CFunctionPointer2StaticFunctionTranslator<TArg0, TArg1, void (*)(TArg0, TArg1)>(fpFunction);
}

//-------------------------------------------------------------------------------------------------------------------------------------------
// 2 arguments, with return value
//-------------------------------------------------------------------------------------------------------------------------------------------

/// Function Pointer ein Argument und ohne Rückgabewert
template<typename TReturn, typename TArg0, typename TArg1>
class CFunctionPointer2R : public CFunctionPointerBase
{
public:
    typedef TReturn (__cdecl* TTranslatorFunctionPointer)(CFunctionPointerBase* pCallbackData, TArg0 arg0, TArg1 arg1);
    TTranslatorFunctionPointer m_fpTranslatorFunction;

	CFunctionPointer2R() : m_fpTranslatorFunction(0)  {}

	CFunctionPointer2R(void* pxObject, void* fpFunction, int iFunctionPointerSize, TTranslatorFunctionPointer pxTranslatorFunction) : 
		CFunctionPointerBase(pxObject, fpFunction, iFunctionPointerSize),
		m_fpTranslatorFunction(pxTranslatorFunction) {}

	operator bool()
	{
		return m_fpTranslatorFunction != 0;
	}

	TReturn operator()(TArg0 arg0, TArg1 arg1)
	{
		return m_fpTranslatorFunction(this, arg0, arg1);
	}
};

/// Function Pointer mit einem Argument und ohne Rückgabewert; Translator für Member-Funktionen
template<typename TReturn, typename TArg0, typename TArg1, typename TObject, typename TFunction>
class CFunctionPointer2RTranslator : public CFunctionPointer2R<TReturn, TArg0, TArg1>
{
public:
	CFunctionPointer2RTranslator(TObject* pxObject, const TFunction& function) : 
	  CFunctionPointer2R<TReturn, TArg0, TArg1>(pxObject, (void*) &function, sizeof(function), Call) {}

    static TReturn Call(CFunctionPointerBase* pCallbackData, TArg0 arg0, TArg1 arg1)
	{
		TObject* pObject = (TObject*) pCallbackData->m_pxObject;
		TFunction &memFunc(*(TFunction*)(void *)(pCallbackData->m_fpMemberFunction));
		return (pObject->*memFunc)(arg0, arg1);
	}
};

/// Function Pointer mit einem Argument und ohne Rückgabewert; Translator für globale (d.h. statische) Funktionen
template<typename TReturn, typename TArg0, typename TArg1, typename TFunction>
class CFunctionPointer2RStaticFunctionTranslator : public CFunctionPointer2R<TReturn, TArg0, TArg1>
{
public:
	CFunctionPointer2RStaticFunctionTranslator(const TFunction& function) : 
	  CFunctionPointer2R<TReturn, TArg0, TArg1>(0, function, 0, Call) {}

    static TReturn Call(CFunctionPointerBase* pCallbackData, TArg0 arg0, TArg1 arg1)
	{
		return (TFunction(pCallbackData->m_fpStaticFunction))(arg0, arg1);
	}
};

/// Create-Funktion: Member-Funktion: ein Argument, kein Rückgabewert
template<typename TObject, typename TReturn, typename TArg0, typename TArg1>
inline
CFunctionPointer2RTranslator<TReturn, TArg0, TArg1, TObject, TReturn (TObject::*)(TArg0, TArg1)>
CreateFunctionPointer2R(TObject* pxObject, TReturn (TObject::*fpFunction)(TArg0, TArg1))
{
    return CFunctionPointer2RTranslator<TReturn, TArg0, TArg1, TObject, TReturn (TObject::*)(TArg0, TArg1)>(pxObject, fpFunction);
}

/// Create-Funktion: statische Funktion: ein Argument, kein Rückgabewert
template<typename TReturn, typename TArg0, typename TArg1>
inline
CFunctionPointer2RStaticFunctionTranslator<TReturn, TArg0, TArg1, TReturn (*)(TArg0, TArg1)>
CreateFunctionPointer2R(TReturn (*fpFunction)(TArg0, TArg1))
{
    return CFunctionPointer2RStaticFunctionTranslator<TReturn, TArg0, TArg1, TReturn (*)(TArg0, TArg1)>(fpFunction);
}


//-------------------------------------------------------------------------------------------------------------------------------------------


#include "baselib/FunctionPointer.inl"

#endif // BASELIB_FUNCTIONPOINTER_H_INCLUDED
