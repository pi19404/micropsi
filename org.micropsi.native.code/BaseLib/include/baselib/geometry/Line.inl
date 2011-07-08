//---------------------------------------------------------------------------------------------------------------------
template<typename VectorType>
CLine<VectorType>::CLine()
{
}
//---------------------------------------------------------------------------------------------------------------------
template<typename VectorType>
CLine<VectorType>::CLine(const VectorType& rvStart, const VectorType& rvEnd)
:	m_vStart(rvStart),
	m_vEnd(rvEnd)
{
}
//---------------------------------------------------------------------------------------------------------------------
template<typename VectorType>
float 
CLine<VectorType>::GetLength() const
{
	return (m_vStart - m_vEnd).Abs();
}
//---------------------------------------------------------------------------------------------------------------------
template<typename VectorType>
VectorType
CLine<VectorType>::GetDirection() const
{
	VectorType vRay = m_vEnd - m_vStart;

	if (vRay.IsZero())
	{
		assert(false && "line has length zero!");
		return vRay;
	}
	else
	{
		return vRay.GetNormalized();
	}
}
//---------------------------------------------------------------------------------------------------------------------
