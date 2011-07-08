//---------------------------------------------------------------------------------------------------------------------
inline
CLevelEditor::ClickMode	
CLevelEditor::GetClickMode() const
{
	return m_eClickMode;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void
CLevelEditor::SetObjectTypeToCreate(const std::string& p_rsType)
{
	m_sObjectTypeToCreate = p_rsType;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void
CLevelEditor::SetRenderingEnabled(bool p_bRender)
{
	m_bRenderingEnabled = p_bRender;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void
CLevelEditor::SetRandomRotationForNewObjects(bool p_bRandomRotation)
{
	m_bRandomRotationForNewObjects = p_bRandomRotation;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void
CLevelEditor::SetVariationToCreate(int p_iVariation)
{
	m_iObjectVariationToCreate = p_iVariation;
}
//---------------------------------------------------------------------------------------------------------------------
