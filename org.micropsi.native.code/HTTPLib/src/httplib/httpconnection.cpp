#include "stdafx.h"
#include "httplib/httpconnection.h"

#include <afxinet.h>
#include "baselib\debugprint.h"

//---------------------------------------------------------------------------------------------------------------------
CHTTPConnection::CHTTPConnection()
{
	DebugPrint("CHTTPConnection ctor");
	m_bIsOpen = false;
	m_bVerbose = false;

	m_pxSession = 0;
	m_pxHttpConnection = 0;
}

//---------------------------------------------------------------------------------------------------------------------
CHTTPConnection::~CHTTPConnection()
{
	Close();
}

//---------------------------------------------------------------------------------------------------------------------
bool	
CHTTPConnection::Open(const char* p_pcServer, int p_iPort)
{
	if(m_bIsOpen)
	{
		Close();
	}

	m_sServerName = p_pcServer;
	m_iPort = p_iPort;

	try
	{
		m_pxSession = new CInternetSession;
		DebugPrint("open connection to %s %d", p_pcServer, m_iPort);
		m_pxHttpConnection = m_pxSession->GetHttpConnection(p_pcServer, (INTERNET_PORT) m_iPort);
		m_bIsOpen = true;
		return true;
	}
	catch (CInternetException* pEx)
	{
		pEx;
		return false;
	}
}

//---------------------------------------------------------------------------------------------------------------------
void
CHTTPConnection::Close()
{
	if(m_bIsOpen)
	{
		m_bIsOpen = false;

		delete m_pxHttpConnection;
		m_pxHttpConnection = 0;
		
		if(m_pxSession)
		{
			m_pxSession->Close();
		}

		delete m_pxSession;
		m_pxSession = 0;
	}
}

//---------------------------------------------------------------------------------------------------------------------
bool
CHTTPConnection::PostRequest(const char* p_pcFileURL, const char* p_pcMsg)
{
	DWORD dwRet;
	try
	{
		CHttpFile* pFile = NULL;
//		DebugPrint("openrequest to %s - %s - %d", m_sServerName.c_str(), p_pcFileURL, m_iPort);
		pFile = m_pxHttpConnection->OpenRequest(CHttpConnection::HTTP_VERB_POST, p_pcFileURL);
//		pFile->AddRequestHeaders("Host: 192.168.100.1\nUser-Agent: blah");
//		DebugPrint("done");

		if(m_bVerbose)
		{
			DebugPrint("Sending to Server:\n\n%s\n\n", p_pcMsg);
		}

		pFile->SendRequest("", 0, (LPVOID) p_pcMsg, (DWORD) strlen(p_pcMsg));
		pFile->QueryInfoStatusCode(dwRet);

		if (dwRet == HTTP_STATUS_OK)
		{
			m_sLastOutput.Clear();

			char szBuff[8192];
			UINT nRead = 0;
			do 
			{
				nRead = pFile->Read(szBuff, 8191);
//				DebugPrint("read %d bytes", nRead);
				szBuff[nRead] = 0;
				m_sLastOutput += szBuff;
//				DebugPrint("read buffer: %s", szBuff);
			}
			while (nRead != 0);
		
			if(m_bVerbose)
			{
				DebugPrint("server returned %s", m_sLastOutput.c_str());
			}
		}

		pFile->Close();
		delete pFile;

		return (dwRet == HTTP_STATUS_OK);
	}
	catch (CInternetException* pEx)
	{
		char sz[1024];
		pEx->GetErrorMessage(sz, 1024);
		DebugPrint("Exception: %s\n", sz);
		m_sLastOutput = sz;
		return false;
	}
}

//---------------------------------------------------------------------------------------------------------------------
const char*	
CHTTPConnection::GetOutput() const
{
	return m_sLastOutput.c_str();
}

//---------------------------------------------------------------------------------------------------------------------
bool				
CHTTPConnection::IsOpen() const
{
	return m_bIsOpen;
}

//---------------------------------------------------------------------------------------------------------------------
void				
CHTTPConnection::SetVerboseDebugOutput(bool p_bVerbose)
{
	m_bVerbose = p_bVerbose;
}

//---------------------------------------------------------------------------------------------------------------------
