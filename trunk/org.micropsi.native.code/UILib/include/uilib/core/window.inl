//---------------------------------------------------------------------------------------------------------------------
inline
WHDL
CWindow::GetWHDL() const								
{ 
	return m_hWnd; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
void				
CWindow::SetSize(int p_iWidth, int p_iHeight)
{ 
	this->SetSize(CSize(p_iWidth, p_iHeight)); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
CSize
CWindow::GetSize() const	
{ 
	return m_xSize; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
void
CWindow::SetMinSize(const CSize& p_rxMinSize)
{ 
	m_xMinSize = p_rxMinSize; 
	AssureMinSize(p_rxMinSize); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
void
CWindow::SetMaxSize(const CSize& p_rxMaxSize)
{ 
	m_xMaxSize = p_rxMaxSize; AssureMaxSize(p_rxMaxSize); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
CSize
CWindow::GetMinSize() const
{ 
	return m_xMinSize; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
CSize
CWindow::GetMaxSize() const
{ 
	return m_xMaxSize; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
CRct
CWindow::GetRect() const
{ 
	return CRct(0,0, m_xSize.cx , m_xSize.cy); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
void
CWindow::SetPos(int p_iX, int p_iY)
{
	this->SetPos(CPnt(p_iX, p_iY)); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
WHDL
CWindow::GetParent() const
{ 
	return m_hParent; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
int
CWindow::NumChildWindows() const	
{ 
	return m_ahSubs.Size(); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
int
CWindow::NumPhysicalChildWindows() const	
{ 
	return m_ahSubs.Size(); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
WHDL
CWindow::GetChild(int p_iIndex) const
{ 
	return m_ahSubs[p_iIndex]; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
WHDL
CWindow::GetPhysicalChild(int p_iIndex) const
{ 
	return m_ahSubs[p_iIndex]; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool
CWindow::AddChild(CWindow* p_pxWindow)
{ 
	return AddChild(p_pxWindow->GetWHDL()); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
void
CWindow::SetStandardCursor()	
{ 
	SetCursor(CMouseCursor::CT_Arrow); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
CMouseCursor::CursorType 
CWindow::GetCursor() const
{ 
	return m_eCursor; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool
CWindow::GetDisabled() const	
{ 
	return m_bDisabled  ||  m_bParentDisabled; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool
CWindow::GetVisible() const	
{ 
	return m_bVisible  &&  m_bParentVisible; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
void
CWindow::SetTransparent(bool p_bTransparent)	
{ 
	m_bTransparent = p_bTransparent; InvalidateWindow(); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool
CWindow::GetTransparent() const	
{ 
	return m_bTransparent  ||  m_bAllWndsTransparent; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool
CWindow::GetIgnoreModals() const	
{ 
	return m_bIgnoreModals; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool
CWindow::GetAlwaysOnTop() const	
{ 
	return m_bAlwaysOnTop; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool
CWindow::GetCanReceiveFocus() const	
{ 
	return m_bCanReceiveFocus; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
void
CWindow::SetCanReceiveFocus(bool p_bCanFocus)	
{ 
	m_bCanReceiveFocus = p_bCanFocus; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool
CWindow::GetIsDesktop() const
{ 
	return m_bDesktop; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool
CWindow::GetWriteAlpha() const
{
	return m_bWriteAlpha; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
CFourCC	
CWindow::GetVisualizationType() const
{ 
	return  m_xVisType; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
CPnt
CWindow::GetRelPos() const
{ 
	return m_xPos; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool
CWindow::OnTimer(int p_iID)	
{ 
	DebugPrint("Warning: unhandled timer %d, p_iID"); 
	return true; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool
CWindow::OnActivateIndirect()
{
	return true; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool
CWindow::OnDeactivateIndirect()	
{ 
	return true; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool
CWindow::OnCharacterKey(int p_iKey, unsigned char p_iModifier)
{
	return false;
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool
CWindow::OnControlKey(int p_iKey, unsigned char p_iModifier)		
{
	return false;
}
//---------------------------------------------------------------------------------------------------------------------
inline
CStr
CWindow::GetDebugString() const
{ 
	return "CWindow"; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
void		
CWindow::SetOnMouseMoveCallback(CFunctionPointer2<CWindow*, const CPnt&>& rxCallback)
{
	m_xOnMouseMoveCallback = rxCallback;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void		
CWindow::SetOnLButtonDownCallback(CFunctionPointer2<CWindow*, const CPnt&>& rxCallback)
{
	m_xOnLButtonDownCallback = rxCallback;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void		
CWindow::SetOnRButtonDownCallback(CFunctionPointer2<CWindow*, const CPnt&>& rxCallback)
{
	m_xOnRButtonDownCallback = rxCallback;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void		
CWindow::SetOnMButtonDownCallback(CFunctionPointer2<CWindow*, const CPnt&>& rxCallback)
{
	m_xOnMButtonDownCallback = rxCallback;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void		
CWindow::SetOnLButtonUpCallback(CFunctionPointer2<CWindow*, const CPnt&>& rxCallback)
{
	m_xOnLButtonUpCallback = rxCallback;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void		
CWindow::SetOnRButtonUpCallback(CFunctionPointer2<CWindow*, const CPnt&>& rxCallback)
{
	m_xOnRButtonUpCallback = rxCallback;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void		
CWindow::SetOnMButtonUpCallback(CFunctionPointer2<CWindow*, const CPnt&>& rxCallback)
{
	m_xOnMButtonUpCallback = rxCallback;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void		
CWindow::SetOnLButtonDoubleClickCallback(CFunctionPointer2<CWindow*, const CPnt&>& rxCallback)
{
	m_xOnLButtonDoubleClickCallback = rxCallback;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void		
CWindow::SetOnRButtonDoubleClickCallback(CFunctionPointer2<CWindow*, const CPnt&>& rxCallback)
{
	m_xOnRButtonDoubleClickCallback = rxCallback;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void		
CWindow::SetOnMButtonDoubleClickCallback(CFunctionPointer2<CWindow*, const CPnt&>& rxCallback)
{
	m_xOnMButtonDoubleClickCallback = rxCallback;
}
//---------------------------------------------------------------------------------------------------------------------
