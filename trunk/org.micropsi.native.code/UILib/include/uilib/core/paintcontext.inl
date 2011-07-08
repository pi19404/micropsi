
//----------------------------------------------------------------------------------------------------------------------
/// Zeichnet einen einzelnen Pixel
inline
void 
CPaintContext::SetPixel(int p_iX, int p_iY, const CColor& p_xColor) const
{
	SetPixel(CPnt(p_iX, p_iY), p_xColor);
}

//----------------------------------------------------------------------------------------------------------------------
/// Zeichnet eine Linie
inline
void 
CPaintContext::DrawLine(int p_iX1, int p_iY1, int p_iX2, int p_iY2, CColor p_xColor) const
{
	DrawLine(CPnt(p_iX1, p_iY1), CPnt(p_iX2, p_iY2), p_xColor);
}

//----------------------------------------------------------------------------------------------------------------------
/// Zeichnet ein nicht-ausgefülltest Rechteck, also nur den Umriss
inline
void 
CPaintContext::DrawRect(const CPnt& p_rxPnt1, const CPnt& p_rxPnt2, CColor p_xColor) const
{
	DrawRect(CRct(p_rxPnt1, p_rxPnt2), p_xColor);
}

//----------------------------------------------------------------------------------------------------------------------
/// Zeichnet ein nicht-ausgefülltest Rechteck, also nur den Umriss
inline
void 
CPaintContext::DrawRect(int p_iX1, int p_iY1, int p_iX2, int p_iY2, CColor p_xColor) const
{
	DrawRect(CRct(p_iX1, p_iY1, p_iX2, p_iY2), p_xColor);
}


//----------------------------------------------------------------------------------------------------------------------
/// Zeichnet ein ausgefülltes Rechteck
inline
void 
CPaintContext::FillRect(const CPnt& p_rxPnt1, const CPnt& p_rxPnt2, CColor p_xColor) const
{
	FillRect(CRct(p_rxPnt1, p_rxPnt2), p_xColor);
}

//----------------------------------------------------------------------------------------------------------------------
/// Zeichnet ein ausgefülltes Rechteck
inline
void 
CPaintContext::FillRect(int p_iX1, int p_iY1, int p_iX2, int p_iY2, CColor p_xColor) const
{
	FillRect(CRct(p_iX1, p_iY1, p_iX2, p_iY2), p_xColor);
}

//----------------------------------------------------------------------------------------------------------------------
