#pragma once
#ifndef HTTPLIB_HTTPCONNECTION_H_INCLUDED
#define HTTPLIB_HTTPCONNECTION_H_INCLUDED

#ifdef DLL_COMPILING
	#define DLLEXPORT __declspec(dllexport)
#else
	#define DLLEXPORT __declspec(dllimport)
#endif

#include "baselib/Str.h"

class CInternetSession;
class CHttpConnection;

class DLLEXPORT CHTTPConnection
{
public:

	CHTTPConnection();
	virtual ~CHTTPConnection();	

	bool			Open(const char* p_pcServer, int p_iPort);
	void			Close();

	bool			IsOpen() const;

	bool			PostRequest(const char* p_pcFileURL, const char* p_pcMsg);
	const char*		GetOutput() const; 

	void			SetVerboseDebugOutput(bool p_bVerbose);

private:

	bool				m_bIsOpen;
	bool				m_bVerbose;

	CStr				m_sServerName;					///< server to connect to
	unsigned long		m_iPort;						///< port on server
	
	CInternetSession*	m_pxSession;
	CHttpConnection*	m_pxHttpConnection;

	CStr				m_sLastOutput;					///< last message returned by server
};


#endif // HTTPLIB_HTTPCONNECTION_H_INCLUDED

