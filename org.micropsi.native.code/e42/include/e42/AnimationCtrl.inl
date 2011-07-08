
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void 
CAnimationCtrl::SetModel(TModelHandle hndModel)
{
    m_hndModel = hndModel;
    //StartAnimation(".default");
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
float 
CAnimationCtrl::GetCurrentAnimationTime() const
{
    return (float)m_pxEngineController->GetEngineTime() - m_fAnimationStartTime;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
float 
CAnimationCtrl::GetCurrentAnimationRemainingTime() const
{
    assert(!m_bLoopAnimation);
    return max(0, m_fAnimationLength - GetCurrentAnimationTime());
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
float 
CAnimationCtrl::GetCurrentAnimationLength() const
{
    return m_fAnimationLength;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
const std::string&
CAnimationCtrl::GetCurrentAnimationName() const
{
    return m_sCurrentAnimationName;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
float 
CAnimationCtrl::GetAnimationLength(const std::string& sAnimationName) const
{
    return m_hndModel->GetAnimationLength(sAnimationName);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
bool 
CAnimationCtrl::AnimationFinished() const
{
    return (GetCurrentAnimationTime() >= m_fAnimationLength);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
