/*******************************************************************************
Matrix.h - Matrizen

	CMatrix:       generische nxm-Matrix (Template)
	CSquareMatrix: generische nxn-Matrix (Template)
	CSquareMatrix2: 2x2-Matrix aka CMat2S
	CSquareMatrix3: 3x3-Matrix aka CMat3S
	CSquareMatrix4: 4x4-Matrix aka CMat4S
*******************************************************************************/
//-----------------------------------------------------------------------------------------------------------------------------------------
//  Matrix.h 
//		Copyright (c) 2002 by Daniel Matzke
//-----------------------------------------------------------------------------------------------------------------------------------------

#pragma once

#ifndef DX9FW_MATRIX_H_INCLUDED
#define DX9FW_MATRIX_H_INCLUDED

#include <assert.h>
#include <d3dx9math.h>

#include "baselib/geometry/CVector.h"
#include "baselib/geometry/Quaternion.h"

//-----------------------------------------------------------------------------------------------------------------------------------------
//-- generic matrix class ----------------------------------------------------
//-----------------------------------------------------------------------------------------------------------------------------------------

template <unsigned uRows, unsigned uCols>
class CMatrix
{
public:
// matrix elements
	float	m_af[uRows*uCols];

	CMatrix();                                                                              // default constructor
	CMatrix(const CMatrix& mSource);                                                        // copy contructor
	const CMatrix& operator=(const CMatrix& mSource);                                       // assignment operator
	~CMatrix();                                                                             // destructor

	void Clear();                                                                           // set all matrix elements to zero
	void Fix();                                                                             // fix elements to -1, 0 and +1

	unsigned Rows() const;                                                                  // get number of rows
	unsigned Cols() const;                                                                  // get number of columns

	float& operator[](const unsigned uElementIdx);                                          // access matrix elements const
	const float& operator()(const unsigned uRow, const unsigned uCol) const;                // access matrix elements const
	
	float& operator()(const unsigned uRow, const unsigned uCol);                            // access matrix elements ref

	const CVector<uCols>& GetRow(unsigned uRow) const;                                      // get matrix-row as vector
	const CVector<uRows> GetCol(unsigned uCol) const;                                       // get matrix-column as vector

	void SetRow(unsigned uRow,const CVector<uCols>& vRow);                                  // set matrix-row to vector
	void SetCol(unsigned iCol,const CVector<uRows>& vCol);                                  // set matrix-column to vector


	bool operator==(const CMatrix<uRows, uCols> mCmp) const;                                // equality operator
	bool operator!=(const CMatrix<uRows, uCols> mCmp) const;                                // unequality operator

	CMatrix<uRows, uCols> operator+(const CMatrix<uRows,uCols>& mAdd) const;                // matrix addition
	CMatrix<uRows, uCols>& operator+=(const CMatrix<uRows,uCols>& mAdd);                    // matrix addition inplace

	CMatrix<uRows, uCols> operator*(const float fMult) const;                               // scalar matrix multiplication
	CMatrix<uRows, uCols>& operator*=(const float fMult);                                   // scalar matrix multiplication inplace
	
	CMatrix<uRows, uCols> operator/(const float fDiv) const;                                // scalar matrix division
	const CMatrix<uRows, uCols>& operator/=(const float fDiv);                              // scalar matrix division inplace


	CMatrix<uRows,uRows> operator*(const CMatrix<uCols, uRows>& mMult) const;               // matrix multiplication mxm = mxn * nxm (random operand Rows-sizes not possible)
	CMatrix<uRows,uRows + 1> operator^(const CMatrix<uCols - 1, uRows + 1>& mMult) const;   // matrix multiplication 34 = 34 * 34 (assume last row is 0 ... 0 1)


//	const CMatrix<uRows,uCols>& operator*=(const CMatrix<uCols, uRows>& mMult);             // matrix multiplication inplace

	CVector<uRows> operator*(const CVector<uCols>& vMult) const;                            // multiply matrix with vector (mx1 = mxn * nx1)
	CVector<uRows> operator^(const CVector<uCols-1>& vMult) const;                          // multiply matrix with vector (assume last row is 0 ... 0 1)


	CMatrix<uCols, uRows> GetTransposed() const;                                            // get matrix transposed

	CMatrix<uRows + 1, uCols> GetRowExtended() const;                                       // extend number of rows by 1, fill with row 0 ... 1
	const CMatrix<uRows, uCols + 1> GetColExtended() const;                                 // extend number of colums by 1, fill with col 0 ... 0
	CMatrix<uRows, uCols + 1> GetColExtended(const CVector<uRows>& vTranslation) const;     // extend number of colums by 1, fill with translation

	void Translate(const CVector<uCols - 1>& vTranslation);                                 // translate matrix inplace
	const CMatrix<uRows, uCols> GetTranslated(const CVector<uCols - 1>& vTranslation) const;// get translated matrix

	void FixElements();                                                                     // Fix Matrix

	bool IsZero();

//	friend ostream& operator<< (ostream& o, const CMatrix<uRows, uCols>& m);                // ostream operator
};

//-----------------------------------------------------------------------------------------------------------------------------------------
//-- square matrix class ----------------------------------------------------
//-----------------------------------------------------------------------------------------------------------------------------------------

template <unsigned uSize>
class CSquareMatrix : public CMatrix<uSize,uSize>
{
public:
	// Row=Y=Zeilenindex, Col=X=Spaltenindex

	CSquareMatrix<uSize>();                                                             // default constructor
	CSquareMatrix<uSize>(const CMatrix<uSize,uSize>& mSource);                          // copy contructor
	const CSquareMatrix<uSize>& operator=(const CSquareMatrix<uSize>& mSource);         // assignment operator
	~CSquareMatrix<uSize>();                                                            // destructor

	
	void SetIdentity();                                                                 // set matrix to identity-matrix
	void SetScale(const float fScale);                                                  // set matrix to scale-matrix
	void SetTranslation(const CVector<uSize-1>& vTranslation);                          // set matrix to translation-matrix

	const CVector<uSize - 1>& GetTranslation() const;									// get translation of matrix
	const CSquareMatrix<uSize+1> GetExtended() const;                                   // get matrix extended by one dimension;
	const CSquareMatrix<uSize+1> GetExtended(const CVector<uSize>& vTranslation) const; // get matrix extended by one dimension, include translation

	unsigned Size() const;                                                              // get matrix-dimension

	void SwapRows(unsigned uRow1, unsigned uRow2);                                      // swap rows (for simplex)

	void Invert();                                                                      // invert matrix
	CSquareMatrix<uSize> GetInverse() const;                                            // calc inverse matrix
	float GetDeterminant() const;                                                       // calculate determinant
	
	void Transpose();                                                                   // transpose matrix
};

//-----------------------------------------------------------------------------------------------------------------------------------------
//-- specilisation: 1x1 element-matrix ---------------------------------------
//-----------------------------------------------------------------------------------------------------------------------------------------

class CSquareMatrix1 : public CSquareMatrix<1>
{
public:
	CSquareMatrix1();                                                                   // repeating default constructor (not inherited)
	CSquareMatrix1(const CMatrix<1, 1>& mSource);                                       // repeating - (not inherited)
	const CSquareMatrix1& operator=(const CMatrix<1, 1>& mSource);                      // repeating assignment operator (not inherited)

	CSquareMatrix1(const float f00);                                                    // construct matrix by float

	static const CSquareMatrix1 mIdentity;
};

//-----------------------------------------------------------------------------------------------------------------------------------------
//-- specilisation: 2x2 element-matrix ---------------------------------------
//-----------------------------------------------------------------------------------------------------------------------------------------

class CSquareMatrix2 : public CSquareMatrix<2>
{
public:
	CSquareMatrix2();                                                                   // repeating default constructor (not inherited)
	CSquareMatrix2(const CMatrix<2, 2>& mSource);                                       // repeating copy constructor (not inherited)
	const CSquareMatrix2& operator=(const CMatrix<2, 2>& mSource);                      // repeating assignment operator (not inherited)

	CSquareMatrix2(	const float f00,	const float f01,                                // construct matrix by 4 floats
					const float f10,	const float f11);

	static const CSquareMatrix2 mIdentity;
};

//-----------------------------------------------------------------------------------------------------------------------------------------
//-- specilisation: 3x3 element-matrix ---------------------------------------
//-----------------------------------------------------------------------------------------------------------------------------------------

class CSquareMatrix3 : public CSquareMatrix<3>
{
public:
	CSquareMatrix3();                                                                   // repeating default constructor (not inherited)
	CSquareMatrix3(const CMatrix<3, 3>& mSource);                                       // repeating copy constructor (not inherited)
	const CSquareMatrix3& operator=(const CMatrix<3, 3>& mSource);                      // repeating assignment operator (not inherited)

	CSquareMatrix3(	const float f00,	const float f01,	const float f02,            // construct matrix by 9 floats
					const float f10,	const float f11,	const float f12,
					const float f20,	const float f21,	const float f22);

	void SetRotationX(const float fRotX);                                               // set matrix to rotation-matrix by x-axis
	void SetRotationY(const float fRotY);                                               // set matrix to rotation-matrix by y-axis
	void SetRotationZ(const float fRotZ);                                               // set matrix to rotation-matrix by z-axis

	void Invert();																		// invert matrix
	CSquareMatrix3 GetInverse() const;													// calc inverse matrix

	static CSquareMatrix3 CalcRotationMatrix(const CQuat& q);                           // build matrix from quaternion

	static const CSquareMatrix3 mIdentity;
};

//-----------------------------------------------------------------------------------------------------------------------------------------
//-- specilisation: 4x4 element-matrix ---------------------------------------
//-----------------------------------------------------------------------------------------------------------------------------------------

class CSquareMatrix4 : public CSquareMatrix<4>
{
public:
	CSquareMatrix4();                                                                   // repeating default constructor (not inherited)
	CSquareMatrix4(const CMatrix<4, 4>& mSource);                                       // repeating copy constructor (not inherited)
	const CSquareMatrix4& operator=(const CMatrix<4, 4>& mSource);                      // repeating assignment operator (not inherited)

	CSquareMatrix4(	const float f00,	const float f01,	const float f02,	const float f03,    // construct matrix by 16 floats
					const float f10,	const float f11,	const float f12,	const float f13,
					const float f20,	const float f21,	const float f22,	const float f23,
					const float f30,	const float f31,	const float f32,	const float f33);


	void Transpose();                                                                   // transpose matrix
	CSquareMatrix4 GetTransposed() const;                                            // get matrix transposed
	CSquareMatrix4 operator*(const CMatrix<4, 4>& mMult) const;                     // matrix multiplication mxm = mxn * nxm (random operand Rows-sizes not possible)
	CVector4 operator*(const CVector<4>& vMult) const;                              // multiply matrix with vector (mx1 = mxn * nx1)
	CVector4 operator^(const CVector<3>& vMult) const;                              // multiply matrix with vector (assume last row is 0 ... 0 1)

	void Invert();                                                                  // invert matrix
	CSquareMatrix4 GetInverse() const;                                              // calc inverse matrix

	void FromQuaternion(const CQuat& q);
	CQuat ToQuaternion();

	CSquareMatrix3 GetRotationMatrix() const;

	static CSquareMatrix4 CalcRotationMatrix(const CQuat& q);
	static CSquareMatrix4 CalcTranslationMatrix(const CVector<3>& vTranslation);
	static CSquareMatrix4 CalcMatrix(const CQuat& q, const CVector<3>& vTranslation);
	static CSquareMatrix4 CalcMatrix(const CMatrix<3, 3>& m, const CVector<3>& vTranslation);

	static const CSquareMatrix4 mIdentity;
};

CVector4 operator*(const CVector<3>& v, const CMatrix<4, 4>& m);
CVector3 operator^(const CVector<3>& v, const CMatrix<4, 4>& m);
CVector4 operator*(const CVector<4>& v, const CMatrix<4, 4>& m);

CVector3 operator*(const CVector<3>& v, const CMatrix<3, 3>& m);

//-----------------------------------------------------------------------------------------------------------------------------------------
// macro for inplace convertion from D3DMATRIX to CSquareMatrix4
#define CMat4(mD3D) (*reinterpret_cast<CSquareMatrix4*>(&(mD3D)))

//-----------------------------------------------------------------------------------------------------------------------------------------
// macro for inplace convertion from D3DMATRIX to const CSquareMatrix4
#define CMat4C(mD3D) (*reinterpret_cast<const CSquareMatrix4*>(&(mD3D)))

//-----------------------------------------------------------------------------------------------------------------------------------------

//-----------------------------------------------------------------------------------------------------------------------------------------
//-- definition of short names ------------------------------------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------------------------------------------------------------
typedef CSquareMatrix1	CMat1S;
typedef CSquareMatrix2	CMat2S;
typedef CSquareMatrix3	CMat3S;
typedef CSquareMatrix4	CMat4S;
typedef CMatrix<4,3>	CMat43;
typedef CMatrix<3,2>	CMat32;

//-----------------------------------------------------------------------------------------------------------------------------------------
#include "baselib/geometry/Matrix.inl"

#endif // DX9FW_MATRIX_H_INCLUDED
