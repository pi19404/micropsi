//---------------------------------------------------------------------------------------------------------------------
inline
CCriticalSection::CCriticalSection()
{
	InitializeCriticalSection(&m_xCriticalSection);
}
//---------------------------------------------------------------------------------------------------------------------
inline
CCriticalSection::~CCriticalSection()
{
	DeleteCriticalSection(&m_xCriticalSection);
}
//---------------------------------------------------------------------------------------------------------------------
inline
void		
CCriticalSection::Enter()
{
	EnterCriticalSection(&m_xCriticalSection);
}
//---------------------------------------------------------------------------------------------------------------------
#if(_WIN32_WINNT >= 0x0400)
inline
bool		
CCriticalSection::TryEnter()
{
	return TryEnterCriticalSection(&m_xCriticalSection) == TRUE;
}
#endif // #if(_WIN32_WINNT >= 0x0400)
//---------------------------------------------------------------------------------------------------------------------
inline
void		
CCriticalSection::Leave()
{
	LeaveCriticalSection(&m_xCriticalSection);
}
//---------------------------------------------------------------------------------------------------------------------
inline
CLock::CLock(CCriticalSection* p_pxCriticalSection)
{
	m_pxCriticalSection = p_pxCriticalSection;
	m_pxCriticalSection->Enter();
}
//---------------------------------------------------------------------------------------------------------------------
inline
CLock::~CLock()
{
	m_pxCriticalSection->Leave();
}
//---------------------------------------------------------------------------------------------------------------------
