//-----------------------------------------------------------------------------------------------------------------------------------------
// default-constructor
template <unsigned uRows>
CVector<uRows>::CVector()
{

}

//-----------------------------------------------------------------------------------------------------------------------------------------
// copy-constructor
template <unsigned uRows>
CVector<uRows>::CVector(const CVector& vSource)
{
	for (unsigned uRow = 0; uRow < uRows; ++uRow)
	{
		m_af[uRow] = vSource.m_af[uRow];
	}
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// assignment operator
template <unsigned uRows>
CVector<uRows>& 
CVector<uRows>::operator=(const CVector& vSource)
{
	for (unsigned uRow = 0; uRow < uRows; ++uRow)
	{
		m_af[uRow] = vSource.m_af[uRow];
	}
	
	return *this;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// destructor
template <unsigned uRows>
CVector<uRows>::~CVector()
{

}

//-----------------------------------------------------------------------------------------------------------------------------------------
// reset all components to zero
template <unsigned uRows>
void 
CVector<uRows>::Clear()
{
	for (unsigned uRow = 0; uRow < uRows; ++uRow)
	{
		m_af[uRow] = 0;
	}
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// return number of elements (dimension)
template <unsigned uRows>
unsigned 
CVector<uRows>::Size()
{
	return uRows;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// get vector element const
template <unsigned uRows>
const float 
CVector<uRows>::operator()(unsigned uRow) const
{
    assert(uRow < uRows);
	return m_af[uRow];
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// get vector ellement as ref
template <unsigned uRows>
float& 
CVector<uRows>::operator()(unsigned uRow)
{
	return m_af[uRow];
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// cast vector to lower dimension vector
/*	
template <unsigned uRows>
CVector<uRows>::operator CVector<uRows - 1>() const
{
	CVector<uRows - 1> vResult;
	for (unsigned uRow = 0; uRow < uRows - 1; ++uRow)
	{
		vResult.m_af[uRow] = m_af[uRow];
	}
	return vResult;
}*/

//-----------------------------------------------------------------------------------------------------------------------------------------
// cast vector to lower dimension vector 
//    (same as operator CVector<uRows-1>(), but inplace)
template <unsigned uRows>
CVector<uRows - 1>& 
CVector<uRows>::GetReduced()
{
	return *reinterpret_cast<CVector<uRows - 1>*>(this);
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// cast vector to lower dimension vector 
//    (same as operator CVector<uRows-1>(), but inplace)
template <unsigned uRows>
const CVector<uRows - 1>& 
CVector<uRows>::GetReduced() const
{
	return *reinterpret_cast<const CVector<uRows - 1>*>(this);
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// get vector extended by one dimension
template <unsigned uRows>
CVector<uRows + 1> 
CVector<uRows>::GetExtended(const float fNewElement) const
{
	CVector<uRows + 1> vResult;
	unsigned uRow = 0;
	for (uRow = 0; uRow < uRows; ++uRow)
	{
		vResult(uRow) = m_af[uRow];
	}
	vResult(uRow) = fNewElement;
	return vResult;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// equality-operator
template <unsigned uRows>
bool 
CVector<uRows>::operator==(const CVector<uRows>& vCmp) const
{
	for (unsigned uRow = 0; uRow < uRows; ++uRow)
	{
		if (m_af[uRow] != vCmp.m_af[uRow])
		{
			return false;
		}
	}
	return true;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// unequality-operator
template <unsigned uRows>
bool 
CVector<uRows>::operator!=(const CVector<uRows>& vCmp) const
{
	for (unsigned uRow = 0; uRow < uRows; ++uRow)
	{
		if (m_af[uRow] != vCmp.m_af[uRow])
		{
			return true;
		}
	}
	return false;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// scalar product
template <unsigned uRows>
CVector<uRows> 
CVector<uRows>::operator*(const float fFact) const
{
	CVector<uRows>	vResult;
	for (unsigned uRow = 0; uRow < uRows; ++uRow)
	{
		vResult.m_af[uRow] = m_af[uRow] * fFact;
	}
	
	return vResult;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// scalar product inplace
template <unsigned uRows>
CVector<uRows>& 
CVector<uRows>::operator*=(const float fFact)
{
	for (unsigned uRow = 0; uRow < uRows; ++uRow)
	{
		m_af[uRow] *= fFact;
	}
	
	return *this;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// scalar division
template <unsigned uRows>
CVector<uRows> 
CVector<uRows>::operator/(float fDiv) const
{
	fDiv = 1.0f / fDiv;
	CVector<uRows>	vResult;
	for (unsigned uRow = 0; uRow < uRows; ++uRow)
	{
		vResult.m_af[uRow] = m_af[uRow] * fDiv;
	}
	
	return vResult;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// scalar division inplace
template <unsigned uRows>
CVector<uRows>& 
CVector<uRows>::operator/=(float fDiv)
{
	fDiv = 1.0f / fDiv;
	for (unsigned uRow = 0; uRow < uRows; ++uRow)
	{
		m_af[uRow] *= fDiv;
	}
	
	return *this;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// vector product
template <unsigned uRows>
float 
CVector<uRows>::operator*(const CVector<uRows>& vMult) const
{
	float fResult = 0;
	for (unsigned uRow = 0; uRow < uRows; ++uRow)
	{
		fResult += m_af[uRow] * vMult.m_af[uRow];
	}
	
	return fResult;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// vector addition
template <unsigned uRows>
CVector<uRows> 
CVector<uRows>::operator+(const CVector<uRows>& vAdd) const
{
	CVector<uRows>	vResult;
	vResult.Clear();
	for (unsigned uRow = 0; uRow < uRows; ++uRow)
	{
		vResult.m_af[uRow] = m_af[uRow] + vAdd.m_af[uRow];
	}
	
	return vResult;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// vector addition inplace
template <unsigned uRows>
CVector<uRows>& 
CVector<uRows>::operator+=(const CVector<uRows>& vAdd)
{
	for (unsigned uRow = 0; uRow < uRows; ++uRow)
	{
		m_af[uRow] += vAdd.m_af[uRow];
	}
	
	return *this;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// vector substraction
template <unsigned uRows>
CVector<uRows> 
CVector<uRows>::operator-(const CVector<uRows>& vSub) const
{
	CVector<uRows>	vResult;
	vResult.Clear();
	for (unsigned uRow = 0; uRow < uRows; ++uRow)
	{
		vResult.m_af[uRow] = m_af[uRow] - vSub.m_af[uRow];
	}
	
	return vResult;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// vector substraction inplace
template <unsigned uRows>
CVector<uRows>& 
CVector<uRows>::operator-=(const CVector<uRows>& vSub)
{
	for (unsigned uRow = 0; uRow < uRows; ++uRow)
	{
		m_af[uRow] -= vSub.m_af[uRow];
	}
	
	return *this;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// crosspruduct
template <unsigned uRows>
CVector<uRows> 
CVector<uRows>::operator^(const CVector<uRows>& vCross) const
{
	CVector<uRows> vResult;
    for (int uRow = 0; uRow < uRows; ++uRow)
    {
        vResult(uRow) = m_af[(uRow + 1) % uRows] * vCross((uRow + uRows - 1) % uRows) - 
                        m_af[(uRow + uRows - 1) % uRows] * vCross((uRow + 1) % uRows);
    }
	return vResult;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// crosspruduct inplace
template <unsigned uRows>
CVector<uRows>& 
CVector<uRows>::operator^=(const CVector<uRows>& vCross)
{
	CVector<uRows> vResult;
    for (int uRow = 0; uRow < uRows; ++uRow)
    {
        vResult(uRow) = m_af[(uRow + 1) % uRows] * vCross((uRow + uRows - 1) % uRows) - 
                        m_af[(uRow + uRows - 1) % uRows] * vCross((uRow + 1) % uRows);
    }
    *this = vResult;
	return *this;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// necation operator
template <unsigned uRows>
CVector<uRows> 
CVector<uRows>::operator-() const
{
    CVector<uRows> vResult;
    for (int uRow = 0; uRow < uRows; ++uRow)
    {
        vResult(uRow) = -m_af[uRow];
    }
    return vResult;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// whatever ...
template <unsigned uRows>
const CVector<uRows>& 
CVector<uRows>::operator+() const
{
    return *this;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// length calculated by manhattan-distance
template <unsigned uRows>
float 
CVector<uRows>::Man() const 
{
	float fResult=0;
	for (unsigned uRow = 0; uRow < uRows; ++uRow)
	{
		fResult += fabsf(m_af[uRow]);
	}
	
	return fResult;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// length calculated by euklid-distance
template <unsigned uRows>
float 
CVector<uRows>::Abs() const 
{
	float fResult = 0;
	for (unsigned uRow = 0; uRow < uRows; ++uRow)
	{
		fResult += m_af[uRow] * m_af[uRow];
	}
	
	return (float)sqrt(fResult);
}

//-----------------------------------------------------------------------------------------------------------------------------------------
template <unsigned uRows>
bool
CVector<uRows>::IsNormalized() const 
{
	return fabsf(Abs() - 1.0f) < 1e-4f;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// length calculated by euklid-distance^2
template <unsigned uRows>
float 
CVector<uRows>::AbsSquare() const 
{
	float fResult = 0;
	for (unsigned uRow = 0; uRow < uRows; ++uRow)
	{
		fResult += m_af[uRow] * m_af[uRow];
	}
	
	return fResult;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// get greatest element (abs)
template <unsigned uRows>
float 
CVector<uRows>::Max() const 
{
	float fResult = 0;
	for (unsigned uRow = 0; uRow < uRows; ++uRow)
	{
		if (fResult < fabsf(m_af[uRow])) 
		{
			fResult = fabsf(m_af[uRow]);
		}
	}
	
	return fResult;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// get smallest element (abs)
template <unsigned uRows>
float 
CVector<uRows>::Min() const
{
	float fResult = FLT_MAX;
	for (unsigned uRow = 0;uRow < uRows; ++uRow)
	{
		if (fResult > fabsf(m_af[uRow])) 
		{
			fResult = fabsf(m_af[uRow]);
		}
	}
	
	return fResult;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// normalize vector
template <unsigned uRows>
void 
CVector<uRows>::Normalize() 
{
    assert(Abs() > 0);
	*this /= Abs();
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// get vector normalized
template <unsigned uRows>
CVector<uRows> 
CVector<uRows>::GetNormalized() const
{
	CVector<uRows> vResult = *this;
    vResult.Normalize();
    return vResult;
}

/*
//-----------------------------------------------------------------------------------------------------------------------------------------
// ostream operator
friend 
ostream& 
CVector<uRows>::operator<< (ostream& o, const CVector<uRows>& v)
{
	for (unsigned uRow = 0; uRow < uRows; ++uRow)
	{
		o << '[';
		o << v(uRow) << "]\n";
	}
	return o;
}*/
//-----------------------------------------------------------------------------------------------------------------------------------------
// Fix

template <unsigned uRows>
void 
CVector<uRows>::Fix(const float fFix)
{
    for (unsigned uRow = 0; uRow < uRows; ++uRow)
        if (fabs(m_af[uRow] - fFix) < 0.000244140625)
            m_af[uRow] = fFix;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// Fix
template <unsigned uRows>
void 
CVector<uRows>::Fix()
{
    Fix(-1.0f);                 Fix(+1.0f);
    Fix(-sinf(PIf * 0.25f));    Fix(+sinf(PIf * 0.25f));
    Fix(-0.5f);                 Fix(+0.5f);
    Fix(0.0f);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// IsZero
template <unsigned uRows>
bool
CVector<uRows>::IsZero() const
{
    for (int i = 0; i < uRows; i++)
    {
        if (m_af[i] != 0) 
            return false;
    }
    return true;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
/** 
	linear interpolation between two vectors (inplace)
*/
template <unsigned uRows>
void 
CVector<uRows>::Lerp(const CVector<uRows>& v1, const CVector<uRows>& v2, float fInterpolationFactor)
{
	float f1 = 1.0f - fInterpolationFactor;
	float f2 = fInterpolationFactor;

    for (int i = 0; i < uRows; i++)
    {
        m_af[i] = v1.m_af[i] * f1 + v2.m_af[i] * f2;
    }
}

//-----------------------------------------------------------------------------------------------------------------------------------------
/** 
	linear interpolation between two vectors
*/
template <unsigned uRows>
CVector<uRows> 
CVector<uRows>::GetLerp(const CVector<uRows>& v1, const CVector<uRows>& v2, float fInterpolationFactor)
{
	CVector<uRows> v;
	v.Lerp(v1, v2, fInterpolationFactor);
	return v;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
//-- spezialisation: 1 element-vector ----------------------------------------
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CVector1::CVector1()
{
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// repeating copy constructor (not inherited)
inline
CVector1::CVector1(const CVector<1>& vSource)
    :  CVector<1>(vSource)
{
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// repeating assignment operator (not inherited)
inline
CVector1& 
CVector1::operator=(const CVector<1>& vSource)
{
	CVector<1>::operator=(vSource);
	return *this;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// construct vector by 1 float
inline
CVector1::CVector1(const float f0)
//			:	m_af({f0,f1})
{
	m_af[0] = f0;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
//-- spezialisation: 2 element-vector ----------------------------------------
//-----------------------------------------------------------------------------------------------------------------------------------------
// repeating default constructor (not inherited)
inline
CVector2::CVector2()
{
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// repeating copy constructor (not inherited)
inline
CVector2::CVector2(const CVector<2>& vSource)
    :  CVector<2>(vSource)
{
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// repeating assignment operator (not inherited)
inline
CVector2& 
CVector2::operator=(const CVector<2>& vSource)
{
	CVector<2>::operator=(vSource);
	return *this;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// construct vector by 2 floats
inline
CVector2::CVector2(const float f0, const float f1)
//			:	m_af({f0, f1})
{
	m_af[0] = f0;	m_af[1] = f1;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
//-- spezialisation: 3 element-vector ----------------------------------------
//-----------------------------------------------------------------------------------------------------------------------------------------
// repeating default constructor (not inherited)
inline
CVector3::CVector3()
{
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// repeating copy constructor (not inherited)
inline
CVector3::CVector3(const CVector<3>& vSource)
{
    m_af[0] = vSource.m_af[0];
    m_af[1] = vSource.m_af[1];
    m_af[2] = vSource.m_af[2];
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// repeating assignment operator (not inherited)
inline
CVector3& 
CVector3::operator=(const CVector<3>& vSource)
{  
    m_af[0] = vSource.m_af[0];
    m_af[1] = vSource.m_af[1];
    m_af[2] = vSource.m_af[2];
    return *this;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// construct vector by 3 floats
inline
CVector3::CVector3(const float f0, const float f1, const float f2)
//			:	m_af({f0, f1, f2})
{
	m_af[0] = f0;	m_af[1] = f1;	m_af[2] = f2;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// equality-operator
inline
bool
CVector3::operator==(const CVector<3>& vCmp) const
{
    return 
        m_af[0] == vCmp.m_af[0] &&
        m_af[1] == vCmp.m_af[1] &&
        m_af[2] == vCmp.m_af[2];
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// unequality-operator
inline
bool 
CVector3::operator!=(const CVector<3>& vCmp) const
{
    return 
        m_af[0] != vCmp.m_af[0] ||
        m_af[1] != vCmp.m_af[1] ||
        m_af[2] != vCmp.m_af[2];
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// scalar product
inline
CVector3 
CVector3::operator*(const float fFact) const
{
    return CVector3(
        m_af[0] * fFact,
        m_af[1] * fFact,
        m_af[2] * fFact);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// scalar product inplace
inline
CVector3& 
CVector3::operator*=(const float fFact)
{
    m_af[0] *= fFact;
    m_af[1] *= fFact;
    m_af[2] *= fFact;
    return *this;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// scalar division
inline
CVector3 
CVector3::operator/(float fDiv) const
{
    fDiv = 1.0f / fDiv;
    return CVector3(
        m_af[0] * fDiv,
        m_af[1] * fDiv,
        m_af[2] * fDiv);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// scalar division inplace
inline
CVector3& 
CVector3::operator/=(float fDiv)
{
    fDiv = 1.0f / fDiv;
    m_af[0] *= fDiv;
    m_af[1] *= fDiv;
    m_af[2] *= fDiv;
    return *this;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// vector product
inline
float 
CVector3::operator*(const CVector<3>& vMult) const
{
    return 
        m_af[0] * vMult.m_af[0] + 
        m_af[1] * vMult.m_af[1] + 
        m_af[2] * vMult.m_af[2];
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// vector addition
inline
CVector3 
CVector3::operator+(const CVector<3>& vAdd) const
{
    return CVector3(
        m_af[0] + vAdd.m_af[0],
        m_af[1] + vAdd.m_af[1],
        m_af[2] + vAdd.m_af[2]);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// vector addition inplace
inline
CVector3& 
CVector3::operator+=(const CVector<3>& vAdd)
{
    m_af[0] += vAdd.m_af[0];
    m_af[1] += vAdd.m_af[1];
    m_af[2] += vAdd.m_af[2];
    return *this;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// vector substraction
inline
CVector3 
CVector3::operator-(const CVector<3>& vSub) const
{
    return CVector3(
        m_af[0] - vSub.m_af[0],
        m_af[1] - vSub.m_af[1],
        m_af[2] - vSub.m_af[2]);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// crosspruduct
inline
CVector3& 
CVector3::operator-=(const CVector<3>& vSub)
{
    m_af[0] -= vSub.m_af[0];
    m_af[1] -= vSub.m_af[1];
    m_af[2] -= vSub.m_af[2];
    return *this;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// crosspruduct
inline
CVector3 
CVector3::operator^(const CVector<3>& vCross) const
{
    return CVector3(
        m_af[1] * vCross.m_af[2] - m_af[2] * vCross.m_af[1],
        m_af[2] * vCross.m_af[0] - m_af[0] * vCross.m_af[2],
        m_af[0] * vCross.m_af[1] - m_af[1] * vCross.m_af[0]);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// crosspruduct inplace
inline
CVector3& 
CVector3::operator^=(const CVector<3>& vCross)
{
    this->CVector3::CVector3(
        m_af[1] * vCross.m_af[2] - m_af[2] * vCross.m_af[1],
        m_af[2] * vCross.m_af[0] - m_af[0] * vCross.m_af[2],
        m_af[0] * vCross.m_af[1] - m_af[1] * vCross.m_af[0]);
    return *this;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// negation operator
inline
CVector3 
CVector3::operator-() const
{
    return CVector3(-m_af[0], -m_af[1], -m_af[2]);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
const CVector3& 
CVector3::operator+() const
{
    return *this;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// get vector extended by one dimension
inline
CVector4 
CVector3::GetExtended(const float fNewElement) const
{
    return CVector4(m_af[0], m_af[1], m_af[2], fNewElement);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// length calculated by euklid-distance
inline
float 
CVector3::Abs() const
{
    return sqrtf(
        m_af[0] * m_af[0] +
        m_af[1] * m_af[1] +
        m_af[2] * m_af[2]);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// length calculated by euklid-distance^2
inline
float 
CVector3::AbsSquare() const
{
    return
        m_af[0] * m_af[0] +
        m_af[1] * m_af[1] +
        m_af[2] * m_af[2];
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// normalize vector
inline
void 
CVector3::Normalize()
{
    float f = Abs();
    assert(f > 0);
    f = 1 / f;
    m_af[0] *= f;
    m_af[1] *= f;
    m_af[2] *= f;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// get normalized vector
inline
CVector3 
CVector3::GetNormalized() const
{
    float f = Abs();
    assert(f > 0);
    f = 1 / f;
    return CVector3(
        m_af[0] * f,
        m_af[1] * f,
        m_af[2] * f);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline 
CVector3::operator D3DXVECTOR3&()
{
	return *(D3DXVECTOR3*)this;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline 
CVector3::operator const D3DXVECTOR3&() const
{
	return *(const D3DXVECTOR3*)this;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline 
CVector3::operator D3DVECTOR&()
{
	return *(D3DVECTOR*)this;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline 
CVector3::operator const D3DVECTOR&() const
{
	return *(const D3DVECTOR*)this;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
//-- spezialisation: 4 element-vector ----------------------------------------
//-----------------------------------------------------------------------------------------------------------------------------------------
// repeating default constructor (not inherited)
inline
CVector4::CVector4()
{
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// repeating copy constructor (not inherited)
inline
CVector4::CVector4(const CVector<4>& vSource)
    :  CVector<4>(vSource)
{
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// repeating assignment operator (not inherited)
inline
CVector4& 
CVector4::operator=(const CVector<4>& vSource)
{
	CVector<4>::operator=(vSource);
	return *this;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// construct vector by 4 floats
inline
CVector4::CVector4(const float f0, const float f1, const float f2, const float f3)
//			:	m_af({f0, f1, f2, f3})
{
	m_af[0] = f0;	
	m_af[1] = f1;	
	m_af[2] = f2;	
	m_af[3] = f3;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CVector4::operator D3DXVECTOR4&()
{
	return *(D3DXVECTOR4*)this;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CVector4::operator const D3DXVECTOR4&() const
{
	return *(const D3DXVECTOR4*)this;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
//-- spezialisation: complex -------------------------------------------------
//-----------------------------------------------------------------------------------------------------------------------------------------
// repeating default constructor (not inherited)
inline
CComplex::CComplex()
{
	
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// repeating copy constructor (not inherited)
inline
CComplex::CComplex(const CVector<2>& vSource)
    :  CVector<2>(vSource)
{

}

//-----------------------------------------------------------------------------------------------------------------------------------------
// repeating assignment operator (not inherited)
inline
CComplex& 
CComplex::operator=(const CVector<2>& vSource)
{
	CVector<2>::operator=(vSource);
	return *this;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// construct vector by 2 floats
inline
CComplex::CComplex(const float fReal,const float fImag)
//			:	m_af({fReal,fImag})
{
	m_af[0] = fReal;	    m_af[1] = fImag;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// construct vector by 1 float
inline
CComplex::CComplex(const float fReal)
//			:	m_af({fReal,0})
{
	m_af[0] = fReal;	m_af[1] = 0;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// multiply complex by real
inline
const CComplex 
CComplex::operator*(const float fReal) const
{
	return CComplex(m_af[0] * fReal, m_af[1] * fReal);
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// multiply complex by real inplace
inline
const CComplex& 
CComplex::operator*=(const float fReal)
{
	m_af[0] *= fReal;
	m_af[1] *= fReal;
	return *this;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// dividie complex by real
inline
const CComplex 
CComplex::operator/(const float fReal) const
{
	return CComplex(m_af[0] / fReal, m_af[1] / fReal);
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// dividie complex by real inplace
inline
const CComplex& 
CComplex::operator/=(const float fReal)
{
	m_af[0] /= fReal;
	m_af[1] /= fReal;
	return *this;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// multiply complex by complex
inline
const CComplex 
CComplex::operator*(const CComplex& cMult) const
{
	return CComplex(m_af[0] * cMult(0) - m_af[1] * cMult(1),
					m_af[0] * cMult(1) + m_af[1] * cMult(0));
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// multiply complex by complex inplace
inline
const CComplex& 
CComplex::operator*=(const CComplex& cMult)
{
	const float f0 = m_af[0] * cMult(0) - m_af[1] * cMult(1);
	m_af[1] = m_af[0] * cMult(1) + m_af[1] * cMult(0);
	m_af[0] = f0;
	return *this;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// divide complex by complex
inline
const CComplex 
CComplex::operator/(const CComplex& cDiv) const
{
	return CComplex(m_af[0] * cDiv(0) + m_af[1] * cDiv(1),
					m_af[1] * cDiv(0) - m_af[0] * cDiv(1));
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// divide complex by complex inplace
inline
const CComplex& 
CComplex::operator/=(const CComplex& cDiv)
{
	const float f0 = m_af[0] * cDiv(0) + m_af[1] * cDiv(1);
	m_af[1] = m_af[1] * cDiv(0) - m_af[0] * cDiv(1);
	m_af[0] = f0;
	return *this;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// access real-component const
inline
const float 
CComplex::real() const
{
	return m_af[0];
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// access real-component ref
inline
float& 
CComplex::real()
{
	return m_af[0];
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// access imaginary-component const
inline
const float 
CComplex::imag() const
{
	return m_af[1];
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// access imaginary-component ref
inline
float& 
CComplex::imag()
{
	return m_af[1];
}

//-----------------------------------------------------------------------------------------------------------------------------------------
