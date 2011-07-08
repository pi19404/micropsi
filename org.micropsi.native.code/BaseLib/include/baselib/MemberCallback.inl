//-------------------------------------------------------------------------------------------------------------------------------------------
inline
CMemberCallback::CMemberCallback()
:   m_pxObject(0),
    m_fpFunction(0),
    m_pxUserData(0),
    m_fpCallFunction(0)
{
}
//-------------------------------------------------------------------------------------------------------------------------------------------
inline
CMemberCallback::CMemberCallback(void* pxObject, void* fpFunction, void* pxUserData, TCallFunctionPointer fpCallFunction)
:   m_pxObject(pxObject),
    m_fpFunction(fpFunction),
    m_pxUserData(pxUserData),
    m_fpCallFunction(fpCallFunction)
{
}
//-------------------------------------------------------------------------------------------------------------------------------------------
inline
CMemberCallback::~CMemberCallback()
{
}
//-------------------------------------------------------------------------------------------------------------------------------------------
template<typename TObject, typename TFunction>
CMemberCallbackTemplate<TObject, TFunction>::CMemberCallbackTemplate(TObject* pxObject, TFunction fpFunction)
:   CMemberCallback(pxObject, *(void**)&fpFunction, 0, Call)
{
}
//-------------------------------------------------------------------------------------------------------------------------------------------
template<typename TObject, typename TFunction>
CMemberCallbackTemplate<TObject, TFunction>::~CMemberCallbackTemplate()
{
}
//-------------------------------------------------------------------------------------------------------------------------------------------
template<typename TObject, typename TFunction>
void 
CMemberCallbackTemplate<TObject, TFunction>::Call(const CMemberCallback* pCallbackData)
{
    ((TObject*)pCallbackData->m_pxObject->*(*(TFunction*)&pCallbackData->m_fpFunction))();
}
//-------------------------------------------------------------------------------------------------------------------------------------------
template<typename TObject, typename TFunction, typename TUserData>
CMemberCallbackUDTemplate<TObject, TFunction, TUserData>::CMemberCallbackUDTemplate(TObject* pxObject, TFunction fpFunction, TUserData* pxUserData)
:   CMemberCallback(pxObject, *(void**)&fpFunction, pxUserData, Call)
{
}
//-------------------------------------------------------------------------------------------------------------------------------------------
template<typename TObject, typename TFunction, typename TUserData>
CMemberCallbackUDTemplate<TObject, TFunction, TUserData>::~CMemberCallbackUDTemplate()
{
}
//-------------------------------------------------------------------------------------------------------------------------------------------
template<typename TObject, typename TFunction, typename TUserData>
void 
CMemberCallbackUDTemplate<TObject, TFunction, TUserData>::Call(const CMemberCallback* pCallbackData)
{
    ((TObject*)pCallbackData->m_pxObject->*(*(TFunction*)&pCallbackData->m_fpFunction))((TUserData*)pCallbackData->m_pxUserData);
}
//-------------------------------------------------------------------------------------------------------------------------------------------
