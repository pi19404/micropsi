//---------------------------------------------------------------------------------------------------------------------
inline
CTriangle::CTriangle() 
{
}

//---------------------------------------------------------------------------------------------------------------------
inline
CTriangle::CTriangle(const CVec3& v0, const CVec3& v1, const CVec3& v2)
{
	b = v0;
	e0 = v1-v0;
	e1 = v2-v0;
}

//---------------------------------------------------------------------------------------------------------------------
inline
CTriangle::CTriangle(const CTriangle& t)
{
    b = t.b;
	e0 = t.e0;
	e1 = t.e1;
} 

//---------------------------------------------------------------------------------------------------------------------
inline
void 
CTriangle::Set(const CVec3& v0, const CVec3& v1, const CVec3& v2) 
{
    b  = v0;
    e0 = v1-v0;
    e1 = v2-v0;
}

//---------------------------------------------------------------------------------------------------------------------
//--- get the face normal of the triangle ---------------------------------
inline
CVec3 
CTriangle::GetNormal(void) const 
{
    CVec3 cross(e0^e1);
    cross.Normalize();
    return cross;
}

//---------------------------------------------------------------------------------------------------------------------
//--- get the midpoint (center of gravity) of the triangle ----------------
inline
CVec3 
CTriangle::GetCenterOfGravity(void) const 
{
    const float oneThird = 1.0f / 3.0f;
    return b + ((e0+e1) * oneThird);
}

//---------------------------------------------------------------------------------------------------------------------
//--- get the plane of the triangle ---------------------------------------
inline
CPlane 
CTriangle::GetPlane(void) const 
{
    return CPlane(b, b+e0, b+e1);
}

//---------------------------------------------------------------------------------------------------------------------
//--- get one the edge points ---------------------------------------------
inline
CVec3 
CTriangle::GetCorner(int i) const
{
    switch (i)
    {
        case 0: return b;
        case 1: return b + e0;
        case 2: return b + e1;
        default: return CVec3(0.0f, 0.0f, 0.0f);
    }
}

//---------------------------------------------------------------------------------------------------------------------
//--- check if and where line intersects triangle -------------------------
//  Taken from Magic Software (http://www.cs.unc.edu/~eberly)
//  Return false if line is parallel to triangle or hits its backside.
//
inline
bool 
CTriangle::IntersectsSingleSided(const CRay& line, float& ipos) 
{

    // Compute plane of triangle, Dot(normal,X-tri.b) = 0 where 'normal' is
    // the plane normal.  If the angle between the line direction and normal
    // is small, then the line is effectively parallel to the triangle.
    const float fTolerance = 1e-04f;
    CVec3 norm(e0^e1);
    float fDenominator = norm * line.m_vDirection;
    //float fLLenSqr     = line.m % line.m;
    //float fNLenSqr     = norm % norm;

    // check if intersecting backface or parallel...
    if (fDenominator >= -fTolerance) return false;

    //if ((fDenominator*fDenominator) <= (fTolerance*fLLenSqr*fNLenSqr)) {
    //    // line and triangle are parallel
    //    return false;
    //}

    // The line is X(t) = line.b + t*line.m.  Compute line parameter t for
    // intersection of line and plane of triangle.  Substitute in the plane
    // equation to get Dot(normal,line.b-tri.b) + t*Dot(normal,line.m)   
    CVec3 kDiff0(line.m_vBase - b);
    float fTime = -(norm * kDiff0) / fDenominator;
    if ((fTime<-fTolerance) || (fTime>(1.0f+fTolerance))) return false;

    // Find difference of intersection point of line with plane and vertex
    // of triangle.
    CVec3 kDiff1(kDiff0 + line.m_vDirection * fTime);

    // Compute if intersection point is inside triangle.  Write
    // kDiff1 = s0*E0 + s1*E1 and solve for s0 and s1.
    float fE00 = e0 * e0;
    float fE01 = e0 * e1;
    float fE11 = e1 * e1;
    float fDet = (float) fabs(fE00*fE11-fE01*fE01);     // = |normal|^2 > 0
    float fR0  = e0 * kDiff1;
    float fR1  = e1 * kDiff1;

    float fS0 = fE11*fR0 - fE01*fR1;
    float fS1 = fE00*fR1 - fE01*fR0;

    if ((fS0>=-fTolerance) && (fS1>=-fTolerance) && (fS0+fS1<=fDet+fTolerance)) {
        // intersection is inside triangle
        ipos = fTime;
        return true;
    } else {
        // intersection is outside triangle
        return false;
    }
}

//---------------------------------------------------------------------------------------------------------------------
//--- check if and where line intersects triangle -------------------------
//  Taken from Magic Software (http://www.cs.unc.edu/~eberly)
//  Return false if line is parallel to triangle
//
inline
bool 
CTriangle::IntersectsDoubleSided(const CRay& line, float& ipos) 
{

    // Compute plane of triangle, Dot(normal,X-tri.b) = 0 where 'normal' is
    // the plane normal.  If the angle between the line direction and normal
    // is small, then the line is effectively parallel to the triangle.
    const float fTolerance = 1e-04f;
    CVec3 norm(e0^e1);
    float fDenominator = norm * line.m_vDirection;
    float fLLenSqr     = line.m_vDirection * line.m_vDirection;
    float fNLenSqr     = norm * norm;

    // check if intersecting backface or parallel...
    if (fDenominator*fDenominator <= fTolerance*fLLenSqr*fNLenSqr) return false;

    //if ((fDenominator*fDenominator) <= (fTolerance*fLLenSqr*fNLenSqr)) {
    //    // line and triangle are parallel
    //    return false;
    //}

    // The line is X(t) = line.b + t*line.m.  Compute line parameter t for
    // intersection of line and plane of triangle.  Substitute in the plane
    // equation to get Dot(normal,line.b-tri.b) + t*Dot(normal,line.m)   
    CVec3 kDiff0(line.m_vBase - b);
    float fTime = -(norm * kDiff0) / fDenominator;
    if ((fTime<-fTolerance) || (fTime>(1.0f+fTolerance))) return false;

    // Find difference of intersection point of line with plane and vertex
    // of triangle.
    CVec3 kDiff1(kDiff0 + line.m_vDirection*fTime);

    // Compute if intersection point is inside triangle.  Write
    // kDiff1 = s0*E0 + s1*E1 and solve for s0 and s1.
    float fE00 = e0 * e0;
    float fE01 = e0 * e1;
    float fE11 = e1 * e1;
    float fDet = (float) fabs(fE00*fE11-fE01*fE01);     // = |normal|^2 > 0
    float fR0  = e0 * kDiff1;
    float fR1  = e1 * kDiff1;

    float fS0 = fE11*fR0 - fE01*fR1;
    float fS1 = fE00*fR1 - fE01*fR0;

    if ((fS0>=-fTolerance) && (fS1>=-fTolerance) && (fS0+fS1<=fDet+fTolerance)) {
        // intersection is inside triangle
        ipos = fTime;
        return true;
    } else {
        // intersection is outside triangle
        return false;
    }
}
//---------------------------------------------------------------------------------------------------------------------


