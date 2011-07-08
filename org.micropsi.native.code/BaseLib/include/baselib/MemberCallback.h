#pragma once

#ifndef BASELIB_MEMBERCALLBACK_H_INCLUDED
#define BASELIB_MEMBERCALLBACK_H_INCLUDED

//-------------------------------------------------------------------------------------------------------------------------------------------
/**
    Klasse, die dem Aufruf von Memberfunktionen dient
    (so eine Art Function-Pointer für MemberFunctions inkl. Objekt und UserData)
    
    enthält alle Informationen für den Callback:
        - Objekt
        - Memberfunktion des Objekts
        - UserData, falls es übergeben werden soll
        - Pointer auf statische Funktion, die den Aufruf der Memberfunktion kapselt
*/
class CMemberCallback
{
public:
    void* m_pxObject;
    void* m_fpFunction;
    void* m_pxUserData;

    typedef void (__cdecl* TCallFunctionPointer)(const CMemberCallback* pCallbackData);
    
    TCallFunctionPointer m_fpCallFunction;


    CMemberCallback();
    CMemberCallback(void* pxObject, void* fpFunction, void* pxUserData, TCallFunctionPointer fpCallFunction);
    ~CMemberCallback();

    void Call() { if (m_fpCallFunction) m_fpCallFunction(this); };
};
//-------------------------------------------------------------------------------------------------------------------------------------------
/**
    Template-Klasse zum Aufruf einer Memberfunktion ohne Userdata
*/
template<typename TObject, typename TFunction>
class CMemberCallbackTemplate : public CMemberCallback
{
public:
    CMemberCallbackTemplate(TObject* pxObject, TFunction fpFunction);
    ~CMemberCallbackTemplate();

    static void Call(const CMemberCallback* pCallbackData);
};
//-------------------------------------------------------------------------------------------------------------------------------------------
/**
    Create-Funktion für MemberCallBack-Klasse ohne Userdata
        - benutzt automatic type discovery
        - Aufruf durch CreateMemberCallback(pxWorld, CWorld::Simulate);
*/
template<typename TObject, typename TFunction>
CMemberCallbackTemplate<TObject, TFunction>
CreateMemberCallback(TObject* pxObject, TFunction fpFunction)
{
    return CMemberCallbackTemplate<TObject, TFunction>(pxObject, fpFunction);
}
//-------------------------------------------------------------------------------------------------------------------------------------------
/**
    Template-Klasse zum Aufruf einer Memberfunktion mit Userdata
*/
template<typename TObject, typename TFunction, typename TUserData>
class CMemberCallbackUDTemplate : public CMemberCallback
{
public:
    CMemberCallbackUDTemplate(TObject* pxObject, TFunction fpFunction, TUserData* pxUserData);
    ~CMemberCallbackUDTemplate();

    static void Call(const CMemberCallback* pCallbackData);
};
//-------------------------------------------------------------------------------------------------------------------------------------------
/**
    Create-Funktion für MemberCallBack-Klasse mit Userdata
        - benutzt automatic type discovery
        - Aufruf durch CreateMemberCallback(pxWorld, CWorld::Simulate, pxContext);
*/
template<typename TObject, typename TFunction, typename TUserData>
CMemberCallbackUDTemplate<TObject, TFunction, TUserData>
CreateMemberCallback(TObject* pxObject, TFunction fpFunction, TUserData* pxUserData)
{
    return CMemberCallbackUDTemplate<TObject, TFunction, TUserData>(pxObject, fpFunction, pxUserData);
}
//-------------------------------------------------------------------------------------------------------------------------------------------

#include "baselib/MemberCallback.inl"

#endif // BASELIB_MEMBERCALLBACK_H_INCLUDED