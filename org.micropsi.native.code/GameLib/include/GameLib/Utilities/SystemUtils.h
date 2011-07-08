/*******************************************************************************
 SystemUtils.h - einfache System-Utility-Funktionen (zB Zeitabfrage, Filekram)
*******************************************************************************/
#pragma once

#ifndef SYSTEMUTILS_H_INCLUDED
#define SYSTEMUTILS_H_INCLUDED

#include <string>

//-------------------------------------------------------------------------------------------------------------------------------------------
namespace Utils
{
    __int64 GetFileLastWriteTimeStamp(const char* pcName);

    double GetSeconds();
    double GetDeltaSeconds(double &d);     // liefert Zeit seit letztem Call, d muss anfangs 0 sein
};
//-------------------------------------------------------------------------------------------------------------------------------------------

#include "SystemUtils.inl"


#endif // SYSTEMUTILS_H_INCLUDED
