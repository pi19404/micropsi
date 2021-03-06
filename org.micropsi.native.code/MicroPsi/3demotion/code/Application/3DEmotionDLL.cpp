#ifdef MICROPSI3DEMOTION_JNI_DLL

#define WIN32_LEAN_AND_MEAN
#include <windows.h> 
#include <jni.h>
#include <process.h>

#include "baselib/str.h"

#include "org_micropsi_eclipse_emotion3d_win32_FaceSurface.h"

#include "Application/3DEmotion.h"
#include "Application/Face.h"


//---------------------------------------------------------------------------------------------------------------------

static char			g_pcCommandLine[8192];
static HINSTANCE	g_hInstance = NULL;

//---------------------------------------------------------------------------------------------------------------------
BOOL APIENTRY 
DllMain( HINSTANCE hModule, DWORD  ul_reason_for_call, LPVOID lpReserved)
{
	g_hInstance = hModule;

	switch (ul_reason_for_call)
	{
		case DLL_PROCESS_ATTACH:
			{
//				DebugPrint("DLL Process Attach");
			}
			break;

		case DLL_THREAD_ATTACH:
			{
//				DebugPrint("DLL Thread Attach");
			}
			break;

		case DLL_THREAD_DETACH:
			{
//				DebugPrint("DLL Thread Detach");
			}
			break;

		case DLL_PROCESS_DETACH:
			{
//				DebugPrint("DLL Process Detach");
			}
			break;
	}
	
	return TRUE;
}


//---------------------------------------------------------------------------------------------------------------------
/**
	this function contains the rendering thread for the DLL-application
	it is not used in the Exe-Application
*/
DWORD WINAPI ThreadRun(LPVOID lpParameter)
{
	assert(g_hInstance != NULL);
	WinMain(g_hInstance, NULL, g_pcCommandLine, 0);
	DebugPrint("rendering thread will now terminate");
	return 0;
}


//---------------------------------------------------------------------------------------------------------------------
JNIEXPORT jint JNICALL 
Java_org_micropsi_eclipse_emotion3d_win32_FaceSurface_initialize(JNIEnv *jni, jobject, jint iHWND, jcharArray array)
{
	HWND hWnd = (HWND) iHWND;
	C3DEmotion::SetParentWindow(hWnd);
	DebugPrint("3DEmotion DLL started: window = %d", iHWND);
	
//	jchar* pcCommandLine = jni->GetCharArrayElements(xCommandLine, 0);

	jchar *body = jni->GetCharArrayElements(array, 0);
	int i;
	int arrayLength = jni->GetArrayLength(array);

	for (i=0; i<arrayLength; i++) 
	{
		g_pcCommandLine[i] = body[i] & 0xFF;
	}
	g_pcCommandLine[i] = '\0';
	jni->ReleaseCharArrayElements(array, body, 0);

	DebugPrint("commandline %s", g_pcCommandLine);

	CStr sCommandLine = g_pcCommandLine;
	int idx = sCommandLine.Find("3demotion.exe");
	if(i<0) 
	{
		idx = sCommandLine.Find("3demotion.dll");
	}
	CStr sWorkingDir = sCommandLine.Mid(0, idx);
	DebugPrint("setting dir to %s", sWorkingDir.c_str());
	C3DEmotion::SetBasePath((sWorkingDir).c_str()); 
	C3DEmotion::SetCommandLine(sCommandLine.c_str());

	DWORD dwThreadId;
	::CreateThread(NULL, 0, ThreadRun, NULL, NULL, &dwThreadId);

	return 0;
}


//---------------------------------------------------------------------------------------------------------------------
JNIEXPORT jint JNICALL 
Java_org_micropsi_eclipse_emotion3d_win32_FaceSurface_resize(JNIEnv *, jobject, jint iW, jint iH) 
{
	while(C3DEmotion::Get() == 0  ||  C3DEmotion::Get()->IsRunning() == false)
	{
//		DebugPrint("going to sleep");
		::Sleep(100);
	}

	HWND hWnd = C3DEmotion::Get()->GetWindowHandle();
//	DebugPrint("Setsize %d %d", iW, iH);
	::SetWindowPos(hWnd, HWND_TOP, 0, 0, (int) iW, (int) iH, SWP_NOZORDER | SWP_NOACTIVATE);

	return 0;
}


//---------------------------------------------------------------------------------------------------------------------
CStr 
ToCStr(JNIEnv *jni, jcharArray array)
{
	jchar *body = jni->GetCharArrayElements(array, 0);
	int arrayLength = jni->GetArrayLength(array);
	char* pcCString = new char[arrayLength+1];

	for (int i=0; i<arrayLength; i++) 
	{
		pcCString[i] = body[i] & 0xFF;
	}
	pcCString[i] = '\0';
	jni->ReleaseCharArrayElements(array, body, 0);	

	CStr sResult = pcCString;
	delete pcCString;

	return sResult;
}

//---------------------------------------------------------------------------------------------------------------------
JNIEXPORT jint JNICALL 
Java_org_micropsi_eclipse_emotion3d_win32_FaceSurface_updatebones(JNIEnv *jni, jobject, jcharArray jcaBoneList, jcharArray jcaWeightList)
{
	CStr sBoneList = ToCStr(jni, jcaBoneList);
	CStr sWeightList = ToCStr(jni, jcaWeightList);

	CDynArray<CStr> asBoneList;
	CDynArray<CStr> asWeightList;
	sBoneList.Split(asBoneList, ",");
	sWeightList.Split(asWeightList, ",");

	if(asBoneList.Size() != asWeightList.Size())
	{
		DebugPrint("Error: Bone list size does not match Weight list size (%d vs. %d)", asBoneList.Size(), asWeightList.Size());
	}

	CFace* pxFace = C3DEmotion::Get()->GetFace();
	for(unsigned int i=0; i<min(asBoneList.Size(), asWeightList.Size()); ++i)
	{
		float fWeight = (float) atof(asWeightList[i].c_str());
		//DebugPrint("set bone '%s' to %.2f (string '%s')", asBoneList[i].c_str(), fWeight, asWeightList[i].c_str());

		if(!pxFace->SetBonePos(asBoneList[i].c_str(), fWeight))
		{
			DebugPrint("failed to set bone '%s' to %.2f", asBoneList[i].c_str(), fWeight);
		}
	}

	return 0;
}

//---------------------------------------------------------------------------------------------------------------------
JNIEXPORT jint JNICALL 
Java_org_micropsi_eclipse_emotion3d_win32_FaceSurface_shutdown(JNIEnv *, jobject) 
{
	DebugPrint("trying to stop render thread");
	C3DEmotion::Get()->RequestShutDown();
	Sleep(1000);
	return 0;
}

//---------------------------------------------------------------------------------------------------------------------

#endif // ifdef MICROPSI3DEMOTION_JNI_DLL
