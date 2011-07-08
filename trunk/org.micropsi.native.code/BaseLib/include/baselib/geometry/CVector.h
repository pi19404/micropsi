/*******************************************************************************
 CVector.h - Vektoren

    CVector:  generischer n-dimensionaler Vektor (Template)
    CVector1: eindimensionaler Vektor, aka CVec1
    CVector2: zweidimensionaler Vektor, aka CVec2
    CVector3: dreidimensionaler Vektor, aka CVec3
    CVector4: vierdimensionaler Vektor, aka CVec4
    CComplex: komplexe Zahl
    CQuat: Quaternion

    ps: heist übrigens CVector.h um Konflikte mit std::vector zu vermeiden
*******************************************************************************/

#ifndef E42_CVECTOR_H_INCLUDED
#define E42_CVECTOR_H_INCLUDED

// noch zu lösen:
//   - Konstruktion der Spezialisierungen mit Initialisierungslisten
// CMatrix4/CMatrix3:
//   - SetRotationXY,SetRotationXZ,SetRotationYZ
//   - GetRotatedXY,GetRotationXZ,SetRotatedYZ
//   - RotateXY,RotateXZ,RotateYZ

#include <assert.h>
#include <math.h>
#include <float.h>
#include <d3dx9math.h>
#include "baselib/constants.h"

//-----------------------------------------------------------------------------------------------------------------------------------------
//-- generic vector class ----------------------------------------------------
//-----------------------------------------------------------------------------------------------------------------------------------------

template <unsigned uRows>
class CVector
{
public:
	float	m_af[uRows];
	
	CVector();                                                          // default-constructor
	CVector(const CVector& vSource);                                    // copy-constructor
	CVector& operator=(const CVector& vSource);				            // assignment operator
	~CVector();                                                         // destructor
	

	void Clear();                                                       // reset all components to zero
	unsigned Size();                                                    // return number of elements (dimension)
	

	const float operator()(unsigned uRow) const;                        // get vector element const
    float& operator()(unsigned uRow);	                                // get vector ellement as ref


	CVector<uRows-1>& GetReduced();                                     // cast vector to lower dimension vector (same as operator CVector<uRows-1>(), but inplace)
	const CVector<uRows-1>& GetReduced() const;                         // cast vector to lower dimension vector (same as operator CVector<uRows-1>(), but inplace)
	CVector<uRows+1> GetExtended(const float fNewElement = 0.0f) const; // get vector extended by one dimension
	

	bool operator==(const CVector<uRows>& vCmp) const;                  // equality-operator
	bool operator!=(const CVector<uRows>& vCmp) const;                  // unequality-operator
	
	CVector<uRows> operator*(const float fFact) const;                  // scalar product
	CVector<uRows>& operator*=(const float fFact);                      // scalar product inplace

	CVector<uRows> operator/(float fDiv) const;                         // scalar division
	CVector<uRows>& operator/=(float fDiv);                             // scalar division inplace
	
	float operator*(const CVector<uRows>& vMult) const;                 // vector product

    CVector<uRows> operator+(const CVector<uRows>& vAdd) const;         // vector addition
	CVector<uRows>& operator+=(const CVector<uRows>& vAdd);             // vector addition inplace
	
	CVector<uRows> operator-(const CVector<uRows>& vSub) const;         // vector substraction
	CVector<uRows>& operator-=(const CVector<uRows>& vSub);             // vector substraction inplace

	CVector<uRows> operator^(const CVector<uRows>& vCross) const;       // crossproduct
	CVector<uRows>& operator^=(const CVector<uRows>& vCross);           // crossproduct inplace

    CVector<uRows> operator-() const;                                   // negation operator
    const CVector<uRows>& operator+() const;

	float Man() const;                                                  // length calculated by manhattan-distance
	float Abs() const;                                                  // length calculated by euklid-distance
	float AbsSquare() const;                                            // length calculated by euklid-distance^2

	float Max() const;                                                  // get greatest element (abs)
	float Min() const;                                                  // get smallest element (abs)

	bool IsNormalized() const;

	void Normalize();                                                   // normalize vector
	CVector<uRows> GetNormalized() const;                               // get normalized vector

    void Fix(const float fFix);                                         // fix components value
    void Fix();                                                         // fix components to some predifened values

    bool IsZero() const;                                                // check if all vector components are 0

	/// linear interpolation between two vectors (inplace)
	void Lerp(const CVector<uRows>& v1, const CVector<uRows>& v2, float fInterpolationFactor);

	/// linear interpolation between two vectors
	static CVector<uRows> GetLerp(const CVector<uRows>& v1, const CVector<uRows>& v2, float fInterpolationFactor);
};

#include "baselib/geometry/CVector.mcr"

//-----------------------------------------------------------------------------------------------------------------------------------------
//-- spezialisation: 1 element-vector ----------------------------------------
//-----------------------------------------------------------------------------------------------------------------------------------------

class CVector1 : public CVector<1>
{
public:
	CVector1();                                                         // repeating default constructor (not inherited)
	CVector1(const CVector<1>& vSource);                                // repeating copy constructor (not inherited)
	CVector1& operator=(const CVector<1>& vSource);                     // repeating assignment operator (not inherited)
	
	CVector1(const float f0);                                           // construct vector by 1 float
};

//-----------------------------------------------------------------------------------------------------------------------------------------
//-- spezialisation: 2 element-vector ----------------------------------------
//-----------------------------------------------------------------------------------------------------------------------------------------

class CVector2 : public CVector<2>
{
public:
	CVector2();                                                         // repeating default constructor (not inherited)
	CVector2(const CVector<2>& vSource);                                // repeating copy constructor (not inherited)
	CVector2& operator=(const CVector<2>& vSource);                     // repeating assignment operator (not inherited)

	CVector2(const float f0,const float f1);                            // construct vector by 2 floats
	

    // define element access for x and y
	ADDDEFINITION_ELEMENTACCESS_X(0)
	ADDDEFINITION_ELEMENTACCESS_Y(1)

    // define element access for u and v
	ADDDEFINITION_ELEMENTACCESS_U(0)
	ADDDEFINITION_ELEMENTACCESS_V(1)

	static const CVector2 vXAxis;
	static const CVector2 vYAxis;
};

//-----------------------------------------------------------------------------------------------------------------------------------------
//-- spezialisation: 3 element-vector ----------------------------------------
//-----------------------------------------------------------------------------------------------------------------------------------------
class CVector4;
class CVector3 : public CVector<3>
{
public:
	CVector3();                                                         // repeating default constructor (is not inherited)
	CVector3(const CVector<3>& vSource);                                // repeating copy constructor (is not inherited)
	CVector3& operator=(const CVector<3>& vSource);                     // repeating assignment operator (is not inherited)

	CVector3(const float f0, const float f1, const float f2);           // construct vector by 3 floats
	

#ifdef DIRECT3D_VERSION
	CVector3& operator=(const D3DVECTOR& vD3D);                         // assign from D3DVECTOR
	CVector3(const D3DVECTOR& vD3D);                                    // construct from D3DVECTOR
	operator D3DVECTOR();                                               // cast to D3DVECTOR
#endif

    // define element access for x,y,z
	ADDDEFINITION_ELEMENTACCESS_X(0)
	ADDDEFINITION_ELEMENTACCESS_Y(1)
	ADDDEFINITION_ELEMENTACCESS_Z(2)


	bool operator==(const CVector<3>& vCmp) const;                         // equality-operator
	bool operator!=(const CVector<3>& vCmp) const;                         // unequality-operator
	
	CVector3 operator*(const float fFact) const;                        // scalar product
	CVector3& operator*=(const float fFact);                            // scalar product inplace

	CVector3 operator/(float fDiv) const;                               // scalar division
	CVector3& operator/=(float fDiv);                                   // scalar division inplace
	
	float operator*(const CVector<3>& vMult) const;                       // vector product

    CVector3 operator+(const CVector<3>& vAdd) const;                     // vector addition
	CVector3& operator+=(const CVector<3>& vAdd);                         // vector addition inplace
	
	CVector3 operator-(const CVector<3>& vSub) const;                     // vector substraction
	CVector3& operator-=(const CVector<3>& vSub);                         // vector substraction inplace

	CVector3 operator^(const CVector<3>& vCross) const;                   // crossproduct
	CVector3& operator^=(const CVector<3>& vCross);                       // crossproduct inplace

    CVector3 operator-() const;                                         // negation operator
    const CVector3& operator+() const;

	CVector4 GetExtended(const float fNewElement = 0.0f) const;         // get vector extended by one dimension

	float Abs() const;                                                  // length calculated by euklid-distance
	float AbsSquare() const;                                            // length calculated by euklid-distance^2

	void Normalize();                                                   // normalize vector
	CVector3 GetNormalized() const;                                     // get normalized vector

	operator D3DXVECTOR3&();
	operator const D3DXVECTOR3&() const;

	operator D3DVECTOR&();
	operator const D3DVECTOR&() const;

	static const CVector3 vXAxis;
	static const CVector3 vYAxis;
	static const CVector3 vZAxis;
};

//-----------------------------------------------------------------------------------------------------------------------------------------
//-- spezialisation: 4 element-vector ----------------------------------------
//-----------------------------------------------------------------------------------------------------------------------------------------

class CVector4 : public CVector<4>
{
public:
	CVector4();                                                             // repeating default constructor (not inherited)
	CVector4(const CVector<4>& vSource);                                    // repeating copy constructor (not inherited)
	CVector4& operator=(const CVector<4>& vSource);                         // repeating assignment operator (not inherited)

	CVector4(const float f0,const float f1,const float f2,const float f3);      // construct vector by 4 floats

	operator D3DXVECTOR4&();
	operator const D3DXVECTOR4&() const;

    // define element access for x,y,z
	ADDDEFINITION_ELEMENTACCESS_X(0)
	ADDDEFINITION_ELEMENTACCESS_Y(1)
	ADDDEFINITION_ELEMENTACCESS_Z(2)
	ADDDEFINITION_ELEMENTACCESS_W(3)
};

//-----------------------------------------------------------------------------------------------------------------------------------------
//-- spezialisation: complex -------------------------------------------------
//-----------------------------------------------------------------------------------------------------------------------------------------

class CComplex : public CVector<2>
{
public:
	CComplex();                                                             // repeating default constructor (not inherited)
	CComplex(const CVector<2>& vSource);                                    // repeating copy constructor (not inherited)
	CComplex& operator=(const CVector<2>& vSource);                         // repeating assignment operator (not inherited)

	CComplex(const float fReal,const float fImag);                          // construct vector by 2 floats
	explicit CComplex(const float fReal);                                   // construct vector by 1 float

    
	const CComplex operator*(const float fReal) const;                      // multiply complex by real
	const CComplex& operator*=(const float fReal);                          // multiply complex by real inplace

    const CComplex operator/(const float fReal) const;                      // dividie complex by real
    const CComplex& operator/=(const float fReal);                          // dividie complex by real inplace

    
	const CComplex operator*(const CComplex& cMult) const;                  // multiply complex by complex
	const CComplex& operator*=(const CComplex& cMult);                      // multiply complex by complex inplace

	const CComplex operator/(const CComplex& cDiv) const;                   // divide complex by complex
	const CComplex& operator/=(const CComplex& cDiv);                       // divide complex by complex inplace

	const float real() const;                                               // access real-component const
	float& real();                                                          // access real-component ref

	const float imag() const;                                               // access imaginary-component const
	float& imag();                                                          // access imaginary-component ref
};


//-----------------------------------------------------------------------------------------------------------------------------------------
//-- definition of short names -----------------------------------------------
//-----------------------------------------------------------------------------------------------------------------------------------------
typedef CVector1	CVec1;
typedef CVector2	CVec2;
typedef CVector3	CVec3;
typedef CVector4	CVec4;


//-----------------------------------------------------------------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------------------------------------------------------------

#include "baselib/geometry/CVector.inl"

//-----------------------------------------------------------------------------------------------------------------------------------------

#endif // E42_CVECTOR_H_INCLUDED
