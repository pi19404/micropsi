
#pragma once
#ifndef BASELIB_THREAD_H_INCLUDED
#define BASELIB_THREAD_H_INCLUDED

/**
	Thread-Klasse, basiert auf dem Artikel "a better thread class" von Tomas Restrepo
	http://www.winterdom.com/dev/ptk/wthread.html

	Ermöglicht, Threads auf (fast) beliebigen nicht-statischen Member-Funktionen von Objekten und auf (fast) beliebigen
	globalen oder statischen Memberfunktionen zu starten.
	Einzige Einschränkungen: die Funktion muss unsigned long als Rückgabetyp haben
							 die Funktion darf nur ein Argument haben, der Typ ist allerding beliebig
	Der Thread wird über globale Funktionen StartNewThread() gestart; man erhält ein Objekt vom Typ CThread, welches 
	nur ein Win32-ThreadHandle kapselt.

	Anwendungsbeispiel:

	class CMyTestClass
	{
		unsigned long aTestFunction(int p_iTargetCount);
	}

	... 

	CMyTestClass o;
	CThread t = StartNewThread(o, o::aTestFunction, 100);
*/

#include <windows.h>
#include <assert.h>

//---------------------------------------------------------------------------------------------------------------------
/**
	CThread	- Kapselt einfach nur ein Win32-Thread-Handle
*/
class CThread
{
public:

	CThread();
	CThread(HANDLE p_hHandle, DWORD p_dwThreadID);
	CThread(const CThread& t);	
	~CThread();

	CThread& operator=(const CThread& t);

	HANDLE	GetHandle() const;
	DWORD	GetThreadID() const;
	bool	IsValid() const;

	DWORD	Resume();
	DWORD	Suspend();
	DWORD	Wait(DWORD timeout = INFINITE);

	void	SetPriority(int iPriority) const;
	int		GetPriority() const;

	void	SetName(const char* pcName);

private:

	HANDLE  m_hThread;
    DWORD   m_dwThreadID;

	bool	CopyHandle(HANDLE p_hHandle);
};

//---------------------------------------------------------------------------------------------------------------------
/**
	Diese Klasse wird nur intern von der Funktion StartNewThread() verwendet. 

	Die CreateThread-Funktion von Windows erwartet als Parameter eine globale Funktion mit der Signatur DWORD function(void*)
	Diese Klasse implementiert eine solche Funktion; über Templateparameter ruft sie intern eine beliebige Funktion auf einem
	beliebigen Objekt eines beliebigen Typs auf. Einzige Randbedingung: diese "beliebige" Funktion muss den Rückgabetype 
	unsigned long haben und genau einen Parameter erwarten, der Typ dieses Parameters ist beliebig.
*/
template <typename ObjectType, typename ArgumentType>
class CThreadImpl
{
public:
    typedef unsigned long (ObjectType::*TThreadFunctionPointer)(ArgumentType);

    CThreadImpl() { }
    CThread CreateThread(ObjectType& rxObj, TThreadFunctionPointer pFunc, ArgumentType arg, bool p_bCreateSuspended=false);

private:
    CThreadImpl(const CThreadImpl& t);
	CThreadImpl& operator=(const CThreadImpl& t);


    struct TThreadParams {
        ObjectType*				m_pxObject;
        TThreadFunctionPointer  m_pFunction;
        ArgumentType			arg;

		TThreadParams(ObjectType* o, TThreadFunctionPointer f, ArgumentType a)
	        : m_pxObject(o), m_pFunction(f), arg(a) {}
    };

	/// ThreadProc, wie sie von der Win32-CreateThread-Funktion verlangt wird
    static DWORD __stdcall ThreadProc(void* param);
};



//---------------------------------------------------------------------------------------------------------------------
/**
	Template-Funktion, um neuen Thread auf einer (fast) beliebigen Member-Funktion eines Objektes zu starten
*/
template <class ObjectType, class FunctionType, class ArgumentType>
CThread StartNewThread(ObjectType& obj, FunctionType pfunc, ArgumentType arg, bool p_bCreateSuspended = false);



//---------------------------------------------------------------------------------------------------------------------
/**
	Diese Klasse wird nur intern von der Funktion StartNewThread() in der Variante für globale Funktionen verwendet.

	Diese Klasse ist ein Wrapper, der eine globale Funktion aufruft. Mit diesem Wrapper läßt sich eine globale Funktion 
	mit der Klasse ThreadImpl verwenden.
*/
template <typename FunctionType, typename ArgumentType>
class CGlobalAdaptor 
{
public:
    explicit CGlobalAdaptor (FunctionType p_pFunction) : m_pFunction(p_pFunction) {};
    DWORD Proc(ArgumentType arg);

private:
    FunctionType m_pFunction;
};


//---------------------------------------------------------------------------------------------------------------------
/**
	Template-Funktion, um neuen Thread auf einer (fast) beliebigen globalen Funktion zu starten
*/
template <class FunctionType, class ArgumentType>
CThread StartNewThread(FunctionType pfunc, ArgumentType arg, bool p_bCreateSuspended = false);

#include "baselib/Thread.inl"

#endif // BASELIB_THREAD_H_INCLUDED

