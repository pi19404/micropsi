//---------------------------------------------------------------------------------------------------------------------
inline
CThread::CThread()
{
	m_hThread	 = NULL;
    m_dwThreadID = 0;
}
//---------------------------------------------------------------------------------------------------------------------
inline
CThread::CThread(HANDLE p_hHandle, DWORD p_dwThreadID)
{
	m_hThread = p_hHandle;
	m_dwThreadID = p_dwThreadID;
}

//---------------------------------------------------------------------------------------------------------------------
inline
CThread::~CThread() 
{
	if(m_hThread != NULL)
	{
		::CloseHandle(m_hThread);
	}
}

//---------------------------------------------------------------------------------------------------------------------
inline
CThread::CThread(const CThread& t)
{
	if(!CopyHandle(t.GetHandle()))
	{
		m_hThread = 0;
	}
	m_dwThreadID = t.m_dwThreadID;
}

//---------------------------------------------------------------------------------------------------------------------
inline
CThread& 
CThread::operator=(const CThread& t)
{
	if(this != &t)
	{
		if(!CopyHandle(t.GetHandle()))
		{
			m_hThread = 0;
		}
		m_dwThreadID = t.m_dwThreadID;
	}
	return *this;
}

//---------------------------------------------------------------------------------------------------------------------
inline
HANDLE 
CThread::GetHandle() const
{
	return m_hThread;
}

//---------------------------------------------------------------------------------------------------------------------
inline
DWORD
CThread::GetThreadID() const
{
	return m_dwThreadID;
}

//---------------------------------------------------------------------------------------------------------------------
inline
bool 
CThread::IsValid() const
{
	return (m_hThread != NULL);
}

//---------------------------------------------------------------------------------------------------------------------
inline
DWORD 
CThread::Resume()
{
	return ::ResumeThread(m_hThread);
}

//---------------------------------------------------------------------------------------------------------------------
inline
DWORD 
CThread::Suspend()
{
	return ::SuspendThread(m_hThread);
}

//---------------------------------------------------------------------------------------------------------------------
inline
DWORD 
CThread::Wait(DWORD timeout)
{
	return ::WaitForSingleObject(m_hThread, timeout);
}

//---------------------------------------------------------------------------------------------------------------------
inline
void
CThread::SetPriority(int iPriority) const
{
	::SetThreadPriority(m_hThread, iPriority);
}

//---------------------------------------------------------------------------------------------------------------------
inline
int
CThread::GetPriority() const
{
	return ::GetThreadPriority(m_hThread);
}

//---------------------------------------------------------------------------------------------------------------------
inline
void 
CThread::SetName(const char* pcName)
{
	assert(false && "doesn't work :(");	// FIXME

	typedef struct tagTHREADNAME_INFO
	{
		DWORD dwType;		// must be 0x1000
		LPCSTR szName;		// pointer to name (in user addr space)
		DWORD dwThreadID;	// thread ID (-1=caller thread)
		DWORD dwFlags;		// reserved for future use, must be zero
	} THREADNAME_INFO;

	THREADNAME_INFO info;
	info.dwType = 0x1000;
	info.szName = pcName;
	info.dwThreadID = m_dwThreadID;
	info.dwFlags = 0;

	__try
	{
		RaiseException( 0x406D1388, 0, sizeof(info)/sizeof(DWORD), (DWORD*)&info );
	}
	__except (EXCEPTION_CONTINUE_EXECUTION)
	{
	}
}

//---------------------------------------------------------------------------------------------------------------------
inline
bool 
CThread::CopyHandle(HANDLE p_hHandle)
{
	BOOL b = ::DuplicateHandle(::GetCurrentProcess(), p_hHandle, ::GetCurrentProcess(), 
		&m_hThread, 0, FALSE, DUPLICATE_SAME_ACCESS);
	return (b != FALSE);    
}

//---------------------------------------------------------------------------------------------------------------------
template <typename ObjectType, typename ArgumentType>
CThread 
CThreadImpl<typename ObjectType, typename ArgumentType>::CreateThread(	ObjectType& rxObj, 
																		TThreadFunctionPointer pFunc, 
																		ArgumentType arg, 
																		bool p_bCreateSuspended)
{
    HANDLE hThread; 
	DWORD id;

    TThreadParams* pxParams = new TThreadParams(&rxObj, pFunc, arg);
	hThread = ::CreateThread (0, 0, ThreadProc, (void*) pxParams, p_bCreateSuspended ? CREATE_SUSPENDED : 0, &id);

	if (hThread == NULL)
    {
	    delete pxParams;
		return CThread(0, 0);
    }

    return CThread(hThread, id);
}

//---------------------------------------------------------------------------------------------------------------------
/// ThreadProc, wie sie von der Win32-CreateThread-Funktion verlangt wird
template <typename ObjectType, typename ArgumentType>
DWORD 
__stdcall 
CThreadImpl<typename ObjectType, typename ArgumentType>::ThreadProc(void* param)
{
    TThreadParams* pt = static_cast<TThreadParams*>(param);
    DWORD ret = ((pt->m_pxObject)->*(pt->m_pFunction))(pt->arg);
    delete pt;
    return ret;
}

//---------------------------------------------------------------------------------------------------------------------
/**
	Template-Funktion, um neuen Thread auf einer (fast) beliebigen Member-Funktion eines Objektes zu starten
*/
template <class ObjectType, class FunctionType, class ArgumentType>
CThread StartNewThread(ObjectType& obj, FunctionType pfunc, ArgumentType arg, bool p_bCreateSuspended)
{
    CThreadImpl<ObjectType, ArgumentType> t;
    return t.CreateThread(obj, pfunc, arg, p_bCreateSuspended);
}


//---------------------------------------------------------------------------------------------------------------------
template <typename FunctionType, typename ArgumentType>
DWORD 
CGlobalAdaptor<typename FunctionType, typename ArgumentType>::Proc(ArgumentType arg) 
{
	DWORD ret = m_pFunction(arg);
	delete this;
    return ret;
}


//---------------------------------------------------------------------------------------------------------------------
/**
	Template-Funktion, um neuen Thread auf einer (fast) beliebigen globalen Funktion zu starten
*/
template <class FunctionType, class ArgumentType>
CThread 
StartNewThread(FunctionType pfunc, ArgumentType arg, bool p_bCreateSuspended)
{
    typedef CGlobalAdaptor<FunctionType, ArgumentType> AdaptorType;
    AdaptorType* adaptor = new AdaptorType(pfunc);

    CThreadImpl<AdaptorType, ArgumentType> t;
    return t.CreateThread(*adaptor, &AdaptorType::Proc, arg, p_bCreateSuspended);
}

//---------------------------------------------------------------------------------------------------------------------
