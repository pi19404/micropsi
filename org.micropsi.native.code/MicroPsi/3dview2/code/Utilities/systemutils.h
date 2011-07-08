/*******************************************************************************
 SystemUtils.h - einfache System-Utility-Funktionen (zB Zeitabfrage, Filekram)
*******************************************************************************/
#pragma once

#ifndef SYSTEMUTILS_H_INCLUDED
#define SYSTEMUTILS_H_INCLUDED

#include "Application/stdinc.h"

#include <string>

//-------------------------------------------------------------------------------------------------------------------------------------------
namespace Utils
{
    bool FileExists(const char* pcName);
    __int64 GetFileLastWriteTimeStamp(const char* pcName);

    double GetSeconds();
    double GetDeltaSeconds(double &d);     // liefert Zeit seit letztem Call, d muss anfangs 0 sein

    std::string RemoveFileExtension(const std::string& sFilename);
};
//-------------------------------------------------------------------------------------------------------------------------------------------

#include "SystemUtils.inl"


#endif // SYSTEMUTILS_H_INCLUDED
