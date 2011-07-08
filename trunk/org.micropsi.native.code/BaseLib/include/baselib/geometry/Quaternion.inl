//-----------------------------------------------------------------------------------------------------------------------------------------
// repeating default constructor (not inherited)
inline
CQuat::CQuat()
{
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// copy constructor 
inline
CQuat::CQuat(const CQuat& qSource)
:	m_vA(qSource.m_vA),
	m_fW(qSource.m_fW)
{
}

//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CQuat::CQuat(float fX, float fY, float fZ, float fW)
:	m_vA(fX, fY, fZ),
	m_fW(fW)
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CQuat::CQuat(const CVector3& vA,const float fW)
:	m_vA(vA),
	m_fW(fW)
{
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// assignment operator 
inline
CQuat& 
CQuat::operator=(const CQuat& qSource)
{
	m_vA = qSource.m_vA;
	m_fW = qSource.m_fW;
	return *this;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
inline
bool 
CQuat::operator==(const CQuat& qSource)
{
	return 
		m_vA == qSource.m_vA &&
		m_fW == qSource.m_fW;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
bool 
CQuat::operator!=(const CQuat& qSource)
{
	return 
		m_vA != qSource.m_vA ||
		m_fW != qSource.m_fW;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void 
CQuat::Conjugate()
{
	m_vA = -m_vA;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void
CQuat::Normalize()
{
	float fNorm = sqrtf(m_vA.AbsSquare() + m_fW * m_fW);

	assert(fNorm != 0);

	m_vA /= fNorm;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
inline
float
CQuat::Magnitude() const
{
	float fNorm = m_vA.AbsSquare() + m_fW * m_fW;
	return sqrtf(fNorm);
}

//-----------------------------------------------------------------------------------------------------------------------------------------
/** 
	create quaternion from axis and angle
*/
inline
void	
CQuat::FromAxisAngle(const CVector3& vAxis, const float fAngle)
{
	const float fSin = static_cast<float>(sin(fAngle * 0.5f));
	m_vA = vAxis * fSin;
	m_fW = static_cast<float>(cos(fAngle * 0.5f));

	Normalize();
}

//-----------------------------------------------------------------------------------------------------------------------------------------
/** 
	convert quaternion to axis and angle
*/
inline
void	
CQuat::ToAxisAngle(CVector3& p_vAxis, float p_fAngle) const
{
	CQuat q = *this;
	q.Normalize();

    float cosA = m_fW;
    p_fAngle = acosf(cosA) * 2.0f;

	float sinA = sqrtf(1.0f - cosA * cosA);

	if (fabs(sinA) < 0.0005f) 
	{
		sinA = 1.0f;
	}

	p_vAxis = m_vA / sinA;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void 
CQuat::Slerp(const CQuat& q1, const CQuat& q2, float fInterpolationFactor)
{
    float fScale1;
    float fScale2;
    CQuat A = q1;
    CQuat B = q2;

    // compute dot product, aka cos(theta):
    float fCosTheta = A.m_vA.x()*B.m_vA.x() + A.m_vA.y()*B.m_vA.y() + A.m_vA.z()*B.m_vA.z() + A.m_fW*B.m_fW;

    if (fCosTheta < 0.0f) 
    {
        // flip start quaternion
        A.m_vA.x() = -A.m_vA.x(); A.m_vA.y() = -A.m_vA.y(); A.m_vA.z() = -A.m_vA.z(); A.m_fW = -A.m_fW;
        fCosTheta = -fCosTheta;
    }

    if ((fCosTheta + 1.0f) > 0.05f) 
    {
        // If the quaternions are close, use linear interploation
        if ((1.0f - fCosTheta) < 0.05f) 
        {
            fScale1 = 1.0f - fInterpolationFactor;
            fScale2 = fInterpolationFactor;
        }
        else 
        { 
            // Otherwise, do spherical interpolation
            float fTheta    = acosf(fCosTheta);
            float fSinTheta = sinf(fTheta);
            fScale1 = sinf( fTheta * (1.0f-fInterpolationFactor) ) / fSinTheta;
            fScale2 = sinf( fTheta * fInterpolationFactor) / fSinTheta;
        }
    }
    else 
    {
        B.m_vA.x() = -A.m_vA.y();
        B.m_vA.y() =  A.m_vA.x();
        B.m_vA.z() = -A.m_fW;			// ---------- is it meant to be that way? ----------
        B.m_fW = A.m_vA.z();
        fScale1 = sinf( PIf * (0.5f - fInterpolationFactor) );
        fScale2 = sinf( PIf * fInterpolationFactor );
    }

    m_vA = A.m_vA * fScale1 + B.m_vA * fScale2;
    m_fW = A.m_fW * fScale1 + B.m_fW * fScale2;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CAxisAngle::CAxisAngle()
{
}

//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CAxisAngle::CAxisAngle(const CVector3& vAxis, float fAngle)
:	m_vAxis(vAxis),
	m_fAngle(fAngle)
{
}

//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CAxisAngle::operator CQuat()
{
	CQuat q; 
	q.FromAxisAngle(m_vAxis, m_fAngle); 
	return q;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CAxisAngle::CAxisAngle(const CAxisAngle& xSource)
:	m_vAxis(xSource.m_vAxis),
	m_fAngle(xSource.m_fAngle)
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CAxisAngle& 
CAxisAngle::operator=(const CAxisAngle& xSource)
{
	m_vAxis = xSource.m_vAxis;
	m_fAngle = xSource.m_fAngle;
	return *this;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
bool 
CAxisAngle::operator==(const CAxisAngle& xSource)
{
	return
		m_vAxis == xSource.m_vAxis &&
		m_fAngle == xSource.m_fAngle;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
bool 
CAxisAngle::operator!=(const CAxisAngle& xSource)
{
	return
		m_vAxis != xSource.m_vAxis ||
		m_fAngle != xSource.m_fAngle;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
