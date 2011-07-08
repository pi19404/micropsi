#include "stdafx.h"

#include "baselib/Utils.h"

using std::string;

namespace Utils
{
//-----------------------------------------------------------------------------------------------------------------------------------------
string 
StrToLower(string s)
{
    for (unsigned int i = 0; i < s.size(); i++)
    {
        if ((s[i] >= 'A') && (s[i] <= 'Z'))
        {
            s[i] += 'a' - 'A';
        }
        else if (s[i] == 'Ö') s[i] = 'ö';
        else if (s[i] == 'Ü') s[i] = 'ü';
        else if (s[i] == 'Ä') s[i] = 'ä';
    }
    return s;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
string 
StrToUpper(string s)
{
    for (unsigned int i = 0; i < s.size(); i++)
    {
        if ((s[i] >= 'a') && (s[i] <= 'z'))
        {
            s[i] -= 'a' - 'A';
        }
        else if (s[i] == 'ö') s[i] = 'Ö';
        else if (s[i] == 'ü') s[i] = 'Ü';
        else if (s[i] == 'ä') s[i] = 'Ä';
    }
    return s;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
char*
CreatePCharString(int iNumCharacters)
{
    return new char[iNumCharacters];
}
//-----------------------------------------------------------------------------------------------------------------------------------------
char*
ClonePCharString(const char* pcSourceString)
{
    if (pcSourceString)
    {
        char* pcDestString = new char[strlen(pcSourceString) + 1];
        strcpy(pcDestString, pcSourceString);
        return pcDestString;
    }
    return NULL;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
DeletePCharString(const char* pcString)
{
    delete [] pcString;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
bool 
IsPowerOf2(int i)
{
    return (i & (i - 1)) == 0;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
bool
GetCommandLineParameter(const std::string& sCommandLine, const std::string& sKey, std::string* psValueOut)
{
    size_t iScanPos = sCommandLine.find(sKey + " ");

	if (iScanPos == -1)
	{
		return false;
	}

	size_t iCmdLineLength = sCommandLine.length();


    // ende des Schlüssels suchen
    while ((sCommandLine[iScanPos] != ' ') && (iScanPos < iCmdLineLength)) {iScanPos++;}

	// anfang des Wertes suchen
    while ((sCommandLine[iScanPos] == ' ') && (iScanPos < iCmdLineLength)) {iScanPos++;}


	string sValue;

    while ((sCommandLine[iScanPos] != ' ') && (iScanPos < iCmdLineLength))
	{
		sValue += sCommandLine[iScanPos];
		iScanPos++;
	}

	*psValueOut = sValue;

	return !sValue.empty();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
bool
GetCommandLineParameterBool(const std::string& sCommandLine, const std::string& sKey, bool* pbValueOut)
{
	string sValue;

	if (GetCommandLineParameter(sCommandLine, sKey, &sValue))
	{
		sValue = StrToLower(sValue);
		*pbValueOut = (sValue == "true" || sValue == "1");

		return true;
	}

	return false;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
bool
GetCommandLineParameterInt(const std::string& sCommandLine, const std::string& sKey, int* piValueOut)
{
	string sValue;

	if (GetCommandLineParameter(sCommandLine, sKey, &sValue))
	{
		*piValueOut = atoi(sValue.c_str());

		return true;
	}

	return false;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
}