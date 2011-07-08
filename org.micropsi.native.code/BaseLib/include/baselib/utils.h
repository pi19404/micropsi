#pragma once

#ifndef BASELIB_UTILS_H_INCLUDED
#define BASELIB_UTILS_H_INCLUDED

#include <string>

namespace Utils
{
    std::string StrToLower(std::string s);  // liefert einen string zurück, in dem alle Großbuchstaben durch Kleinbuchstaben ersetzt sind
    std::string StrToUpper(std::string s);  // liefert einen string zurück, in dem alle Kleinbuchstaben durch Großbuchstaben ersetzt sind
    // (nein: ist nicht standardmäßig auf std::string definiert)


    // diese drei Funktionen wurden hauptsächlich für die Funktion ClonePCharString geschrieben
    // die anderen Funktionen sind er Vollständigkeit halber implementiert; 
    // schließlich soll Allokation und Deallokation an derselben Stelle erfolgen
    char* CreatePCharString(int iNumCharacters);            ///< erzeugt einen nullterminierten Character-String in der übergebenen Länge
    char* ClonePCharString(const char* pcSourceString);     ///< kopiert einen nullterminierten Character-String 
    void DeletePCharString(const char* pcString);           ///< löscht einen nullterminierten Character-String 


    bool IsPowerOf2(int i);                                 ///< prüft, ob es sich bei der übergebenen Zahl um eine Zweierpotenz handelt


	// Parsing der Kommandozeile nach dem Schema "[sKey] [sValue] [sKey] [sValue] [sKey] [sValue]"
	bool GetCommandLineParameter(const std::string& sCommandLine, const std::string& sKey, std::string* psValueOut);
	bool GetCommandLineParameterBool(const std::string& sCommandLine, const std::string& sKey, bool* pbValueOut);
	bool GetCommandLineParameterInt(const std::string& sCommandLine, const std::string& sKey, int* piValueOut);
}

#include "baselib/utils.inl"

#endif // BASELIB_UTILS_H_INCLUDED