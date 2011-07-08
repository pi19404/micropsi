//---------------------------------------------------------------------------------------------------------------------
inline
dWorld*	
CDynamicsWorld::GetODEWorld() const
{
	return m_pWorld;
}
//---------------------------------------------------------------------------------------------------------------------
inline
CDynamicsWorld::operator dWorldID() const 
{ 
	return m_pWorld->id(); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
void	
CDynamicsWorld::SetErrorReductionParameter(double dERP)
{
	m_pWorld->setERP(dERP);
}
//---------------------------------------------------------------------------------------------------------------------
inline
double	
CDynamicsWorld::GetErrorReductionParameter() const
{
	return m_pWorld->getERP();
}
//---------------------------------------------------------------------------------------------------------------------
inline
void	
CDynamicsWorld::SetConstraintForceMixing(double dCFM)
{
	m_pWorld->setCFM(dCFM);
}
//---------------------------------------------------------------------------------------------------------------------
inline
double	
CDynamicsWorld::GetConstraintForceMixing() const
{
	return m_pWorld->getCFM();
}
//---------------------------------------------------------------------------------------------------------------------
inline
void	
CDynamicsWorld::SetContactSurfaceLayer(double dLayer)
{
	dWorldSetContactSurfaceLayer(*m_pWorld, dLayer);
}
//---------------------------------------------------------------------------------------------------------------------
inline
double	
CDynamicsWorld::GetContactSurfaceLayer() const
{
	return dWorldGetContactSurfaceLayer(*m_pWorld);
}
//---------------------------------------------------------------------------------------------------------------------
inline
void	
CDynamicsWorld::SetUseQuickStep(bool bQuickStep)
{
	m_bQuickStep = bQuickStep;
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool	
CDynamicsWorld::GetUseQuickStep() const
{
	return m_bQuickStep;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void	
CDynamicsWorld::SetQuickStepNumIterations(int iNum)
{
	dWorldSetQuickStepNumIterations(*m_pWorld, iNum);
}
//---------------------------------------------------------------------------------------------------------------------
inline
int		
CDynamicsWorld::GetQuickStepNumIterations() const
{
	return dWorldGetQuickStepNumIterations(*m_pWorld);
}
//---------------------------------------------------------------------------------------------------------------------
