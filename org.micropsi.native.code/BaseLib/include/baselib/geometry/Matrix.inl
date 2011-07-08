//-----------------------------------------------------------------------------------------------------------------------------------------
// default constructor
template <unsigned uRows, unsigned uCols>
CMatrix<uRows, uCols>::CMatrix()
{
	
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// copy contructor
template <unsigned uRows, unsigned uCols>
CMatrix<uRows, uCols>::CMatrix(const CMatrix& mSource)
{
	for (unsigned uElementIdx = 0; uElementIdx < uRows * uCols; ++uElementIdx)
	{
		m_af[uElementIdx] = mSource.m_af[uElementIdx];
	}
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// assignment operator
template <unsigned uRows, unsigned uCols>
const CMatrix<uRows, uCols>& 
CMatrix<uRows, uCols>::operator=(const CMatrix& mSource)
{
	for (unsigned uElementIdx = 0;uElementIdx < uRows * uCols; ++uElementIdx)
	{
		m_af[uElementIdx] = mSource.m_af[uElementIdx];
	}
	
	return *this;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// destructor
template <unsigned uRows, unsigned uCols>
CMatrix<uRows, uCols>::~CMatrix()
{
	
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// set all matrix elements to zero
template <unsigned uRows, unsigned uCols>
void 
CMatrix<uRows, uCols>::Clear()
{
	for (unsigned uElementIdx = 0; uElementIdx < uRows * uCols; ++uElementIdx)
	{
		m_af[uElementIdx] = 0;
	}
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// fix elements to -1, 0 and +1
template <unsigned uRows, unsigned uCols>
void 
CMatrix<uRows, uCols>::Fix()
{
	// sehr kleine Werte auf 0 setzen
	for (unsigned uElementIdx = 0;uElementIdx < uRows * uCols; ++uElementIdx)
	{
		if (fabsf(m_af[uElementIdx]) < 0.01f)
		{
			m_af[uElementIdx] = 0.0f;
			continue;
		}
		if (fabsf(m_af[uElementIdx] + 1.0f) < 0.01f)
		{
			m_af[uElementIdx] = -1.0f;
			continue;
		}
		if (fabsf(m_af[uElementIdx] - 1.0f) < 0.01f)
		{
			m_af[uElementIdx] = +1.0f;
		}
	}
}


//-----------------------------------------------------------------------------------------------------------------------------------------
// get number of rows
template <unsigned uRows, unsigned uCols>
unsigned 
CMatrix<uRows, uCols>::Rows() const
{
	return uRows;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// get number of columns
template <unsigned uRows, unsigned uCols>
unsigned 
CMatrix<uRows, uCols>::Cols() const
{
	return uCols;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// access matrix elements const
template <unsigned uRows, unsigned uCols>
float& 
CMatrix<uRows, uCols>::operator[](const unsigned uElementIdx) 
{
	assert(uElementIdx < uRows * uCols);
	return m_af[uElementIdx];
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// access matrix elements const
template <unsigned uRows, unsigned uCols>
const float& 
CMatrix<uRows, uCols>::operator()(const unsigned uRow, const unsigned uCol) const
{
	assert(uCol < uCols);
	assert(uRow < uRows);
	return m_af[uCol + uCols * uRow];
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// access matrix elements ref
template <unsigned uRows, unsigned uCols>
float& 
CMatrix<uRows, uCols>::operator()(const unsigned uRow, const unsigned uCol)
{
	assert(uCol < uCols);
	assert(uRow < uRows);
	return m_af[uCol + uCols * uRow];
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// get matrix-row as vector
template <unsigned uRows, unsigned uCols>
const CVector<uCols>& 
CMatrix<uRows, uCols>::GetRow(unsigned uRow) const
{
	assert(uRow < uRows);
	return *(CVector<uCols>*)(&m_af[uCols * uRow]);
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// get matrix-column as vector
template <unsigned uRows, unsigned uCols>
const CVector<uRows> 
CMatrix<uRows, uCols>::GetCol(unsigned uCol) const
{
	assert(uCol < uCols);
	CVector<uRows> vCol;
	for (unsigned uRow = 0;uRow < uRows; ++uRow)
	{
		vCol(uRow) = m_af[uRow * uCols + uCol];
	}
	return vCol;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// set matrix-row to vector
template <unsigned uRows, unsigned uCols>
void 
CMatrix<uRows, uCols>::SetRow(unsigned uRow, const CVector<uCols>& vRow)
{
	for (unsigned uCol = 0; uCol < uCols; ++uCol)
	{
		m_af[uRow * uCols + uCol] = vRow(uCol);
	}
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// set matrix-column to vector
template <unsigned uRows, unsigned uCols>
void 
CMatrix<uRows, uCols>::SetCol(unsigned uCol, const CVector<uRows>& vCol)
{
	for (unsigned uRow = 0; uRow < uRows; ++uRow)
	{
		m_af[uRow * uCols + uCol] = vCol(uRow);
	}
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// equality operator
template <unsigned uRows, unsigned uCols>
bool 
CMatrix<uRows, uCols>::operator==(const CMatrix<uRows, uCols> mCmp) const
{
	for (unsigned uElementIdx = 0; uElementIdx < uRows * uCols; ++uElementIdx)
	{
		if (m_af[uElementIdx] != mCmp.m_af[uElementIdx])
		{
			return false;
		}
	}
	return true;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// unequality operator
template <unsigned uRows, unsigned uCols>
bool 
CMatrix<uRows, uCols>::operator!=(const CMatrix<uRows, uCols> mCmp) const
{
	for (unsigned uElementIdx = 0; uElementIdx < uRows * uCols; ++uElementIdx)
	{
		if (m_af[uElementIdx] != mCmp.m_af[uElementIdx])
		{
			return true;
		}
	}
	return false;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// matrix addition
template <unsigned uRows, unsigned uCols>
CMatrix<uRows, uCols> 
CMatrix<uRows, uCols>::operator+(const CMatrix<uRows, uCols>& mAdd) const
{
	CMatrix<uRows, uCols> mResult;
	for (unsigned uElementIdx = 0; uElementIdx < uRows * uCols; ++uElementIdx)
	{
		mResult.m_af[uElementIdx] = m_af[uElementIdx]+
											mAdd.m_af[uElementIdx];
	}
	return mResult;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// matrix addition inplace
template <unsigned uRows, unsigned uCols>
CMatrix<uRows, uCols>& 
CMatrix<uRows, uCols>::operator+=(const CMatrix<uRows, uCols>& mAdd)
{
	for (unsigned uElementIdx = 0; uElementIdx < uRows * uCols; ++uElementIdx)
	{
		m_af[uElementIdx] += mAdd.m_af[uElementIdx];
	}
	return *this;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// scalar matrix multiplication
template <unsigned uRows, unsigned uCols>
CMatrix<uRows, uCols> 
CMatrix<uRows, uCols>::operator*(const float fMult) const
{
	CMatrix<uRows, uCols> mResult;
	for (unsigned uElementIdx = 0; uElementIdx < uRows * uCols; ++uElementIdx)
	{
		mResult.m_af[uElementIdx] = m_af[uElementIdx] * fMult;
	}
	return mResult;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// scalar matrix multiplication inplace
template <unsigned uRows, unsigned uCols>
CMatrix<uRows, uCols>& 
CMatrix<uRows, uCols>::operator*=(const float fMult)
{
	for (unsigned uElementIdx = 0; uElementIdx < uRows * uCols; ++uElementIdx)
	{
		m_af[uElementIdx] *= fMult;
	}
	return *this;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// scalar matrix division
template <unsigned uRows, unsigned uCols>
CMatrix<uRows, uCols> 
CMatrix<uRows, uCols>::operator/(const float fDiv) const
{
	CMatrix<uRows, uCols> mResult;
	const float fMult = 1 / fDiv;
	for (unsigned uElementIdx = 0; uElementIdx < uRows * uCols; ++uElementIdx)
	{
		mResult.m_af[uElementIdx] = m_af[uElementIdx] * fMult;
	}
	return mResult;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// scalar matrix division inplace
template <unsigned uRows, unsigned uCols>
const CMatrix<uRows, uCols>& 
CMatrix<uRows, uCols>::operator/=(const float fDiv)
{
	const float fMult = 1 / fDiv;
	for (unsigned uElementIdx = 0; uElementIdx<uRows * uCols; ++uElementIdx)
	{
		m_af[uElementIdx] *= fMult;
	}
	return *this;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// matrix multiplication mxm = mxn * nxm (random operand Rows-sizes not possible)
template <unsigned uRows, unsigned uCols>
CMatrix<uRows, uRows> 
CMatrix<uRows, uCols>::operator*(const CMatrix<uCols, uRows>& mMult) const
{
	CMatrix<uRows, uRows> mResult;
	for (unsigned uResultRow = 0; uResultRow < uRows; ++uResultRow)
	{
		for (unsigned uResultCol = 0; uResultCol < uRows; ++uResultCol)
		{
			mResult(uResultRow, uResultCol) = 0;
			for (unsigned uCol = 0; uCol < uCols; ++uCol)
			{
				mResult(uResultRow, uResultCol) +=
						operator()(uResultRow, uCol) * mMult(uCol, uResultCol);
			}
		}
	}
	return mResult;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// matrix multiplication 34 = 34 * 34 (assume last row is 0 ... 0 1)
template <unsigned uRows, unsigned uCols>
CMatrix<uRows, uRows + 1> 
CMatrix<uRows, uCols>::operator^(const CMatrix<uCols - 1, uRows + 1>& mMult) const
{
	const unsigned uRealRows = uRows + 1;
	const unsigned uRealCols = uCols;
	CMatrix<iRealRows - 1, uRealRows> mResult;

	for (unsigned uResultRow = 0; uResultRow < uRealRows - 1; ++uResultRow)
	{
		unsigned uResultCol = 0;
		for (uResultCol = 0; uResultCol < uRealRows - 1; ++uResultCol)
		{
			mResult(uResultRow, uResultCol) = 0;
			for (unsigned uCol = 0; iCol < uRealCols - 1; ++uCol)
			{
				mResult(uResultRow, uResultCol) +=
						operator()(uResultRow, uCol) * mMult(uCol, uResultCol);
			}
			// uCol == uRealCols - 1
			//mResult(uResultRow, uResultCol) += operator()(uResultRow, uCol) * 0;
		}
		mResult(uResultRow, uResultCol) = 0;
		unsigned uCol = 0;
		for (uCol = 0; uCol < uRealCols - 1; ++uCol)
		{
			mResult(uResultRow, uResultCol)+=
					operator()(uResultRow, uCol) * mMult(uCol, uResultCol);
		}
		// uCol==uRealCols-1
		mResult(uResultRow, uResultCol)+=
				operator()(uResultRow, uCol);// * 1;
	}

	return mResult;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// matrix multiplication inplace
// (only possible with nxn matrices, not implemented there, 
//	because would overwrite all *='s of CMatrix and m1*=m2 
//	wouldn't be faster then m1=m1*m2)

/*	
template <unsigned uRows, unsigned uCols>
const CMatrix<uRows,uCols>& 
CMatrix<uRows, uCols>::operator*=(const CMatrix<uCols, uRows>& mMult)
{
	CMatrix<uSize> mThis(*this);
	for (unsigned u = 0; u < uSize; ++u)
	{
		for (unsigned v = 0; v < uSize; ++v)
		{
			operator()(u, v) = mThis(u, 0) * mMult(k, v)
			for (unsigned w = 1; w < uSize; w++)
			{
				operator()(u, v) += mThis(u, w) * mMult(w, v);
			}
		}
	}
	return *this;
}
*/

//-----------------------------------------------------------------------------------------------------------------------------------------
// multiply matrix with vector (mx1=mxn*nx1)
template <unsigned uRows, unsigned uCols>
CVector<uRows> 
CMatrix<uRows, uCols>::operator*(const CVector<uCols>& vMult) const
{
	CVector<uRows> vResult;
	for (unsigned uResultRow = 0; uResultRow < uRows; ++uResultRow)
	{
		vResult(uResultRow) = 0;
		for (unsigned uCol = 0; uCol < uCols; ++uCol)
		{
			vResult(uResultRow) += operator()(uResultRow, uCol) * vMult(uCol);
		}
	}
	return vResult;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// multiply matrix with vector (assume last row is 0 ... 0 1)
template <unsigned uRows, unsigned uCols>
CVector<uRows> 
CMatrix<uRows, uCols>::operator^(const CVector<uCols - 1>& vMult) const
{
	CVector<uRows> vResult;
	for (unsigned uResultRow = 0;uResultRow < uRows; ++uResultRow)
	{
		vResult(uResultRow) = 0;
		unsigned uCol = 0;
		for (uCol = 0; uCol < uCols - 1; ++uCol)
		{
			vResult(uResultRow) += operator()(uResultRow, uCol) * vMult(uCol);
		}
		vResult(uResultRow) += operator()(uResultRow, uCol);
	}
	return vResult;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// get matrix transposed
template <unsigned uRows, unsigned uCols>
CMatrix<uCols, uRows> 
CMatrix<uRows, uCols>::GetTransposed() const
{
	CMatrix<uCols, uRows> mResult;
	for (unsigned uRow = 0; uRow < uRows; ++uRow)
	{
		for (unsigned uCol = 0; uCol < uCols; ++uCol)
		{
			mResult(uCol, uRow) = operator()(uRow, uCol);
		}
	}
	return mResult;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// extend number of rows by 1, fill with row 0 ... 1
template <unsigned uRows, unsigned uCols>
CMatrix<uRows + 1,uCols> 
CMatrix<uRows, uCols>::GetRowExtended() const
{
	CMatrix<uRows + 1, uCols> mResult;
	unsigned uRow = 0;
	for (uRow = 0; uRow < uRows; ++uRow)
	{
		for (unsigned uCol = 0; uCol < uCols; ++uCol)
		{
			mResult(uRow, uCol) = operator()(uRow, uCol);
		}
	}
	unsigned uCol = 0;
	for (uCol = 0; uCol < uCols - 1; ++uCol)
	{
		mResult(uRow, uCol) = 0;
	}
	mResult(uRow, uCol) = 1;

	return mResult;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// extend number of colums by 1, fill with col 0 ... 0
template <unsigned uRows, unsigned uCols>
const CMatrix<uRows,uCols+1> 
CMatrix<uRows, uCols>::GetColExtended() const
{
	CMatrix<uRows + 1, uCols> mResult;
	for (unsigned uRow = 0; uRow < uRows; ++uRow)
	{
		unsigned uCol = 0;
		for (uCol = 0; uCol < uCols - 1; ++uCol)
		{
			mResult(uRow, uCol) = operator()(uRow, uCol);
		}
		mResult(uRow, uCol) = 0;
	}

	return mResult;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// extend number of colums by 1, fill with translation
template <unsigned uRows, unsigned uCols>
CMatrix<uRows,uCols + 1> 
CMatrix<uRows, uCols>::GetColExtended(const CVector<uRows>& vTranslation) const
{
	CMatrix<uRows+1,uCols> mResult;
	for (unsigned uRow = 0; uRow < uRows; ++uRow)
	{
		unsigned uCol = 0;
		for (uCol = 0; uCol < uCols - 1; ++uCol)
		{
			mResult(uRow, uCol) = operator()(uRow, uCol);
		}
		mResult(uRow, uCol) = vTranslation(uRow);
	}

	return mResult;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// translate matrix inplace
template <unsigned uRows, unsigned uCols>
void 
CMatrix<uRows, uCols>::Translate(const CVector<uCols-1>& vTranslation)
{
	for (unsigned uCol = 0; uCol < uCols - 1; ++uCol)
	{
		m_af[uCols * (uRows - 1) + uCol] += vTranslation.m_af[uCol];
	}
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// get translated matrix
template <unsigned uRows, unsigned uCols>
const CMatrix<uRows,uCols> 
CMatrix<uRows, uCols>::GetTranslated(const CVector<uCols-1>& vTranslation) const
{
	CMatrix<uRows, uCols> mResult(*this);
	for (unsigned uCol = 0; uCol < uCols - 1; ++uCol)
	{
		mResult.m_af[uCols * (uRows - 1) + uCol] += vTranslation.m_af[uCol];
	}
	return mResult;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// Fix Matrix
template <unsigned uRows, unsigned uCols>
void 
CMatrix<uRows, uCols>::FixElements()
{
	for (unsigned uElement = 0; uElement < uRows * uCols; ++uElement)
	{
		if (fabsf(m_af[uElement]) < 0.0001f) m_af[uElement] = 0.0f;
		else if (fabsf(m_af[uElement] - 1) < 0.0001f) m_af[uElement] = +1.0f;
		else if (fabsf(m_af[uElement] + 1) < 0.0001f) m_af[uElement] = -1.0f;
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// check if all members are 0
template <unsigned uRows, unsigned uCols>
bool
CMatrix<uRows, uCols>::IsZero()
{
	for (unsigned uElement = 0; uElement < uRows * uCols; ++uElement)
	{
		if (m_af[uElement] != 0) return false;
	}
	return true;
}
/*
//-----------------------------------------------------------------------------------------------------------------------------------------
// ostream operator
template <unsigned uRows, unsigned uCols>
friend ostream& 
CMatrix<uRows, uCols>::operator<< (ostream& o, const CMatrix<uRows, uCols>& m)
{
	for (unsigned uRow = 0; uRow < uRows; ++uRow)
	{
		o << '[';
		for (unsigned uCol = 0; uCol < uCols - 1; ++uCol)
		{
			o << m(uRow, uCol) << "\t";
		}
		o << m(uRow, uCol) << "]\n";
	}
	return o;
}*/
//-----------------------------------------------------------------------------------------------------------------------------------------
//-- square matrix class ----------------------------------------------------
//-----------------------------------------------------------------------------------------------------------------------------------------
// default constructor
template <unsigned uSize>
CSquareMatrix<uSize>::CSquareMatrix<uSize>()
{
	
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// copy contructor
template <unsigned uSize>
CSquareMatrix<uSize>::CSquareMatrix<uSize>(const CMatrix<uSize, uSize>& mSource)
	:  CMatrix<uSize, uSize>(mSource)
{

}

//-----------------------------------------------------------------------------------------------------------------------------------------
// assignment operator
template <unsigned uSize>
const CSquareMatrix<uSize>& 
CSquareMatrix<uSize>::operator=(const CSquareMatrix<uSize>& mSource)
{
	for (unsigned u = 0; u < uSize * uSize; ++u)
	{
		m_af[u] = mSource.m_af[u];
	}
	
	return *this;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// destructor
template <unsigned uSize>
CSquareMatrix<uSize>::~CSquareMatrix<uSize>()
{
	
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// set matrix to identity-matrix
template <unsigned uSize>
void 
CSquareMatrix<uSize>::SetIdentity()
{
	unsigned uElementIdx = 0;
	for (unsigned u = 0; u < uSize - 1; ++u)
	{
		m_af[uElementIdx++] = 1;
		for (unsigned v = 0; v < uSize; ++v)
		{
			m_af[uElementIdx++] = 0;
		}
	}
	m_af[uElementIdx] = 1;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// set matrix to scale-matrix
template <unsigned uSize>
void 
CSquareMatrix<uSize>::SetScale(const float fScale)
{
	unsigned uElementIdx = 0;
	for (unsigned u = 0; u < uSize - 1; ++u)
	{
		m_af[uElementIdx++] = fScale;
		for (unsigned v = 0; v < uSize; ++v)
		{
			m_af[uElementIdx++] = 0;
		}
	}
	m_af[uElementIdx] = fScale;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// set matrix to translation-matrix
template <unsigned uSize>
void 
CSquareMatrix<uSize>::SetTranslation(const CVector<uSize - 1>& vTranslation)
{
	SetIdentity();
	for (unsigned u = 0; u < uSize - 1; ++u)
	{
		m_af[uSize * (uSize - 1) + u] = vTranslation.m_af[u];
	}
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// get translation of matrix
template <unsigned uSize>
const CVector<uSize - 1>&
CSquareMatrix<uSize>::GetTranslation() const
{
	return *(CVector<uSize - 1>*)(m_af + uSize * (uSize - 1));
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// get matrix extended by one dimension
template <unsigned uSize>
const CSquareMatrix<uSize + 1> 
CSquareMatrix<uSize>::GetExtended() const
{
	CSquareMatrix<uSize + 1> mResult;
	for (unsigned u = 0; u < uSize; ++u)
	{
		for (unsigned v = 0; v < uSize; ++v)
		{
			mResult(u, v)=operator()(u, v);
		}
		mResult(u, uSize) = 0;
	}
	for (unsigned v = 0; v < uSize; ++v)
	{
		mResult(uSize, v) = 0;
	}
	mResult(uSize, uSize) = 1;
	return mResult;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// get matrix extended by one dimension, include translation
template <unsigned uSize>
const CSquareMatrix<uSize + 1> 
CSquareMatrix<uSize>::GetExtended(const CVector<uSize>& vTranslation) const
{
	CSquareMatrix<uSize + 1> mResult;
	for (unsigned u = 0; u < uSize; ++u)
	{
		for (unsigned v = 0; v < uSize; ++v)
		{
			mResult(u, v) = operator()(u, v);
		}
		mResult(u, uSize) = vTranslation(u);
	}
	for (unsigned v = 0; v < uSize; ++v)
	{
		mResult(uSize, v) = 0;
	}
	mResult(uSize, uSize) = 1;
	return mResult;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// get matrix-dimension
template <unsigned uSize>
unsigned 
CSquareMatrix<uSize>::Size() const
{
	return uSize;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// swap rows
template <unsigned uSize>
void 
CSquareMatrix<uSize>::SwapRows(unsigned uRow1, unsigned uRow2)
{
	float fSwap;
	for (unsigned uCol = 0; uCol < uSize; ++uCol)
	{
		fSwap = this->operator()(uRow1, uCol);
		this->operator()(uRow1, uCol) = this->operator()(uRow2, uCol);
		this->operator()(uRow2, uCol) = fSwap;
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// invert matrix
template <unsigned uSize>
void 
CSquareMatrix<uSize>::Invert()
{
	FixElements();
	CSquareMatrix<uSize>& mIdentity = *this;
	CSquareMatrix<uSize> mResult;
	mResult.SetIdentity();

	const float fEpsilon = 1e-7f;

	// mIdentity zu in Dreiecksmatrix umwandeln
	for (unsigned uCol = 0; uCol < uSize - 1; ++uCol)
	{
		// falls Element auf Hauptdiagonale = 0, Zeilen austauschen
		if (fabsf(mIdentity(uCol, uCol)) < fEpsilon)
		{
			// find one with no 0
			unsigned u = 0;
			for (u = uCol + 1; u < uSize; ++u)
			{
				if (fabsf(mIdentity(u, uCol)) >= fEpsilon)
				{
					mIdentity.SwapRows(u, uCol);
					mResult.SwapRows(u, uCol);
					break;
				}
			}
			if (u == uSize)
			{
				// Error ...
				assert(false);
				continue;
			}
		}

		for (unsigned uRow = uCol + 1; uRow < uSize; ++uRow)
		{
			float fFact = -mIdentity(uRow, uCol) / mIdentity(uCol, uCol);

			if (fFact != 0)
			{
				for (unsigned uCol2 = uCol + 1; uCol2 < uSize; ++uCol2)
				{
					mIdentity(uRow, uCol2) += mIdentity(uCol, uCol2) * fFact;
				}
				for (unsigned uCol2 = 0; uCol2 < uSize; ++uCol2)
				{
					mResult(uRow, uCol2) += mResult(uCol, uCol2) * fFact;
				}
			}
		}
	}

	// Dreiecksmatrix bilden (nach oben)
	for (unsigned uCol = 0; uCol < uSize - 1; ++uCol)
	{
		for (unsigned uRow = uCol + 1; uRow < uSize; ++uRow)
		{
			assert(mIdentity((uSize - 1) - uCol, (uSize - 1) - uCol) != 0);

			float fFact = -mIdentity((uSize - 1) - uRow, (uSize - 1) - uCol) / mIdentity((uSize - 1) - uCol, (uSize - 1) - uCol);

			if (fFact != 0)
			{
				mIdentity((uSize - 1) - uRow, (uSize - 1) - uCol) += mIdentity((uSize - 1) - uCol, (uSize - 1) - uCol) * fFact;

				for (unsigned uCol2 = 0; uCol2 < uSize; ++uCol2)
				{
					mResult((uSize - 1) - uRow, uCol2) += mResult((uSize - 1) - uCol, uCol2) * fFact;
				}
			}
		}
	}


	for (unsigned uRow = 0; uRow < uSize; ++uRow)
	{
		assert (mIdentity(uRow, uRow) != 0);

		for (unsigned uCol = 0; uCol < uSize; ++uCol)
		{
			mResult(uRow, uCol) /= mIdentity(uRow, uRow);
		}
	}

	*this = mResult;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// invert matrix
template <unsigned uSize>
CSquareMatrix<uSize> 
CSquareMatrix<uSize>::GetInverse() const
{
	CSquareMatrix<uSize> mInverse = *this;
	mInverse.Invert();
	return mInverse;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// calculate determinante
template <unsigned uSize>
float 
CSquareMatrix<uSize>::GetDeterminant() const
{
	CSquareMatrix<uSize> mTriangle = *this;
	mTriangle.FixElements();

	const float fEpsilon = 1e-7f;

	// Dreiecksmatrix bilden
	for (unsigned uCol = 0; uCol < uSize - 1; ++uCol)
	{
		if (fabsf(mTriangle(uCol, uCol)) < fEpsilon)
		{
			// find one with no 0
			unsigned u = 0;
			for (u = uCol + 1; u < uSize; ++u)
			{
				if (mTriangle(u, uCol) != 0)
				{
					mTriangle.SwapRows(u, uCol);
					break;
				}
			}
			if (u == uSize)
			{
				// Error ...
				assert(false);
				continue;
			}
		}

		for (unsigned uRow = uCol + 1; uRow < uSize; ++uRow)
		{
			float fFact = -mTriangle(uRow, uCol) / mTriangle(uCol, uCol);

			if (fFact != 0)
			{
				for (unsigned uCol2 = uCol + 1; uCol2 < uSize; ++uCol2)
				{
					mTriangle(uRow, uCol2) += mTriangle(uCol, uCol2) * fFact;
				}
			}
		}
	}

	float fDet = m_af[0];
	for (unsigned u = 1; u < uSize; ++u)
	{
		fDet *= mTriangle[u * (uSize + 1)];
	}

	return fDet;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// transpose matrix
template <unsigned uSize>
void 
CSquareMatrix<uSize>::Transpose()
{
	float fSwap;
	for (unsigned u = 1; u < uSize; ++u)
	{
		for (unsigned v = 0; v < u; ++v)
		{
			fSwap = operator()(u, v);
			operator()(u, v) = operator()(v, u);
			operator()(v, u) = fSwap;
		}
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
//-- specilisation: 1x1 element-matrix ---------------------------------------
//-----------------------------------------------------------------------------------------------------------------------------------------
// repeating default constructor (not inherited)
inline
CSquareMatrix1::CSquareMatrix1()
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// repeating - (not inherited)
inline
CSquareMatrix1::CSquareMatrix1(const CMatrix<1, 1>& mSource)
:   CSquareMatrix<1>(mSource)
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// repeating assignment operator (not inherited)
/*	
inline
const CSquareMatrix1& 
CSquareMatrix1::operator=(const CSquareMatrix<1>& mSource)
{
	CSquareMatrix<1>::operator=(mSource);
	return *this;
}
*/
//-----------------------------------------------------------------------------------------------------------------------------------------
// construct matrix by float
inline
CSquareMatrix1::CSquareMatrix1(const float f00)
//			:	m_af({f00})
{
	m_af[0] = f00;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
//-- specilisation: 2x2 element-matrix ---------------------------------------
//-----------------------------------------------------------------------------------------------------------------------------------------
// repeating default constructor (not inherited)
inline
CSquareMatrix2::CSquareMatrix2()
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// repeating copy constructor (not inherited)
inline
CSquareMatrix2::CSquareMatrix2(const CMatrix<2, 2>& mSource)
	:  CSquareMatrix<2>(mSource)
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// repeating assignment operator (not inherited)
/*
inline
const CSquareMatrix2& 
CSquareMatrix2::operator=(const CSquareMatrix<2>& mSource)
{
	CSquareMatrix<2>::operator=(mSource);
	return *this;
}
*/
//-----------------------------------------------------------------------------------------------------------------------------------------
// construct matrix by 4 floats
inline
CSquareMatrix2::CSquareMatrix2(	
		const float f00,	const float f01,
		const float f10,	const float f11)
/*	:	m_af({	f00,f01,
						f10,f11})*/
{
	m_af[0]=f00;	m_af[1]=f01;
	m_af[2]=f10;	m_af[2]=f11;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
//-- specilisation: 3x3 element-matrix ---------------------------------------
//-----------------------------------------------------------------------------------------------------------------------------------------
// repeating default constructor (not inherited)
inline
CSquareMatrix3::CSquareMatrix3()
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// repeating copy constructor (not inherited)
inline
CSquareMatrix3::CSquareMatrix3(const CMatrix<3, 3>& mSource)
	:  CSquareMatrix<3>(mSource)
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// repeating assignment operator (not inherited)
inline
const CSquareMatrix3& 
CSquareMatrix3::operator=(const CMatrix<3, 3>& mSource)
{
	CMatrix<3, 3>::operator=(mSource);
	return *this;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// construct matrix by 9 floats
inline
CSquareMatrix3::CSquareMatrix3(	const float f00,	const float f01,	const float f02,
								const float f10,	const float f11,	const float f12,
								const float f20,	const float f21,	const float f22)
/*		:	m_af({	f00,f01,f02,
							f10,f11,f12,
							f20,f21,f22})*/
{
	m_af[0] = f00;	m_af[1] = f01;	m_af[2] = f02;
	m_af[3] = f10;	m_af[4] = f11;	m_af[5] = f12;
	m_af[6] = f20;	m_af[7] = f21;	m_af[8] = f22;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// set matrix to rotation-matrix by x-axis
inline
void 
CSquareMatrix3::SetRotationX(const float fRotX)
{
	const float fSin = static_cast<float>(sin(fRotX));
	const float fCos = static_cast<float>(cos(fRotX));
	m_af[0] = 1;		m_af[1] = 0;		m_af[2] = 0;
	m_af[3] = 0;		m_af[4] = fCos;		m_af[5] = -fSin;
	m_af[6] = 0;		m_af[7] = fSin;		m_af[8] = fCos;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// set matrix to rotation-matrix by y-axis
inline
void 
CSquareMatrix3::SetRotationY(const float fRotY)
{
	const float fSin = static_cast<float>(sin(fRotY));
	const float fCos = static_cast<float>(cos(fRotY));
	m_af[0] = fCos;		m_af[1] = 0;		m_af[2] = fSin;
	m_af[3] = 0;		m_af[4] = 1;		m_af[5] = 0;
	m_af[6] = -fSin;	m_af[7] = 0;		m_af[8] = fCos;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// set matrix to rotation-matrix by z-axis
inline
void 
CSquareMatrix3::SetRotationZ(const float fRotZ)
{
	const float fSin = static_cast<float>(sin(fRotZ));
	const float fCos = static_cast<float>(cos(fRotZ));
	m_af[0] = fCos;	m_af[1] = -fSin;	m_af[2] = 0;
	m_af[3] = fSin;	m_af[4] = fCos;		m_af[5] = 0;
	m_af[6] = 0;	m_af[7] = 0;		m_af[8] = 1;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// build matrix from quaternion
inline
CSquareMatrix3 
CSquareMatrix3::CalcRotationMatrix(const CQuat& q)
{
	return CSquareMatrix3(  1 - 2 * q.m_vA.y() * q.m_vA.y() - 2 * q.m_vA.z() * q.m_vA.z(),		2 * q.m_vA.x() * q.m_vA.y() + 2 * q.m_vA.z() * q.m_fW,			2 * q.m_vA.x() * q.m_vA.z() - 2 * q.m_vA.y() * q.m_fW,
								2 * q.m_vA.x() * q.m_vA.y() - 2 * q.m_vA.z() * q.m_fW,		1 - 2 * q.m_vA.x() * q.m_vA.x() - 2 * q.m_vA.z() * q.m_vA.z(),		2 * q.m_vA.y() * q.m_vA.z() + 2 * q.m_vA.x() * q.m_fW,
								2 * q.m_vA.x() * q.m_vA.z() + 2 * q.m_vA.y() * q.m_fW,			2 * q.m_vA.y() * q.m_vA.z() - 2 * q.m_vA.x() * q.m_fW,		1 - 2 * q.m_vA.x() * q.m_vA.x() - 2 * q.m_vA.y() * q.m_vA.y());
}

//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void 
CSquareMatrix3::Invert()
{
	D3DXMATRIXA16 mat4x4(m_af[0], m_af[1], m_af[2], 0, 
						 m_af[3], m_af[4], m_af[5], 0,
						 m_af[6], m_af[7], m_af[8], 0,
						 0,		  0,       0,       1);

	D3DXMatrixInverse(&mat4x4, 0, &mat4x4);

	m_af[0] = mat4x4._11;	m_af[1] = mat4x4._12;	m_af[2] = mat4x4._13;
	m_af[3] = mat4x4._21;	m_af[4] = mat4x4._22;	m_af[5] = mat4x4._23;
	m_af[6] = mat4x4._31;	m_af[7] = mat4x4._32;	m_af[8] = mat4x4._33;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CSquareMatrix3 
CSquareMatrix3::GetInverse() const
{
	D3DXMATRIXA16 mat4x4(m_af[0], m_af[1], m_af[2], 0, 
						 m_af[3], m_af[4], m_af[5], 0,
						 m_af[6], m_af[7], m_af[8], 0,
						 0,		  0,       0,       1);

	D3DXMatrixInverse(&mat4x4, 0, &mat4x4);

	return CSquareMatrix3(
			mat4x4._11, mat4x4._12, mat4x4._13,
			mat4x4._21, mat4x4._22, mat4x4._23,
			mat4x4._31, mat4x4._32, mat4x4._33);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
//-- specilisation: 4x4 element-matrix ---------------------------------------
//-----------------------------------------------------------------------------------------------------------------------------------------
// repeating default constructor (not inherited)
inline
CSquareMatrix4::CSquareMatrix4()
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// repeating copy constructor (not inherited)
inline
CSquareMatrix4::CSquareMatrix4(const CMatrix<4, 4>& mSource)
	:  CSquareMatrix<4>(mSource)
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// repeating assignment operator (not inherited)
inline
const CSquareMatrix4& 
CSquareMatrix4::operator=(const CMatrix<4, 4>& mSource)
{
	m_af[0] = mSource.m_af[0];
	m_af[1] = mSource.m_af[1];
	m_af[2] = mSource.m_af[2];
	m_af[3] = mSource.m_af[3];

	m_af[4] = mSource.m_af[4];
	m_af[5] = mSource.m_af[5];
	m_af[6] = mSource.m_af[6];
	m_af[7] = mSource.m_af[7];

	m_af[8] = mSource.m_af[8];
	m_af[9] = mSource.m_af[9];
	m_af[10] = mSource.m_af[10];
	m_af[11] = mSource.m_af[11];

	m_af[12] = mSource.m_af[12];
	m_af[13] = mSource.m_af[13];
	m_af[14] = mSource.m_af[14];
	m_af[15] = mSource.m_af[15];

	return *this;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// construct matrix by 16 floats
inline
CSquareMatrix4::CSquareMatrix4(	const float f00,	const float f01,	const float f02,	const float f03,
								const float f10,	const float f11,	const float f12,	const float f13,
								const float f20,	const float f21,	const float f22,	const float f23,
								const float f30,	const float f31,	const float f32,	const float f33)
/*			:	m_af({	f00, f01, f02, f03, 
								f10, f11, f12, f13, 
								f20, f21, f22, f23, 
								f30, f31, f32, f33})*/
{
	m_af[ 0] = f00;	m_af[ 1] = f01;	m_af[ 2] = f02;	m_af[ 3] = f03;
	m_af[ 4] = f10;	m_af[ 5] = f11;	m_af[ 6] = f12;	m_af[ 7] = f13;
	m_af[ 8] = f20;	m_af[ 9] = f21;	m_af[10] = f22;	m_af[11] = f23;
	m_af[12] = f30;	m_af[13] = f31;	m_af[14] = f32;	m_af[15] = f33;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void 
CSquareMatrix4::FromQuaternion(const CQuat& q)
{
	m_af[ 0] =	1 - 2 * q.m_vA.y() * q.m_vA.y() - 2 * q.m_vA.z() * q.m_vA.z();
	m_af[ 1] =		2 * q.m_vA.x() * q.m_vA.y() + 2 * q.m_vA.z() * q.m_fW;
	m_af[ 2] =		2 * q.m_vA.x() * q.m_vA.z() - 2 * q.m_vA.y() * q.m_fW; 
	m_af[ 3] = 0;								  
													
	m_af[ 4] =		2 * q.m_vA.x() * q.m_vA.y() - 2 * q.m_vA.z() * q.m_fW;
	m_af[ 5] =	1 - 2 * q.m_vA.x() * q.m_vA.x() - 2 * q.m_vA.z() * q.m_vA.z();
	m_af[ 6] =		2 * q.m_vA.y() * q.m_vA.z() + 2 * q.m_vA.x() * q.m_fW;
	m_af[ 7] = 0;								  
													
	m_af[ 8] =		2 * q.m_vA.x() * q.m_vA.z() + 2 * q.m_vA.y() * q.m_fW;
	m_af[ 9] =		2 * q.m_vA.y() * q.m_vA.z() - 2 * q.m_vA.x() * q.m_fW;
	m_af[10] =	1 - 2 * q.m_vA.x() * q.m_vA.x() - 2 * q.m_vA.y() * q.m_vA.y();
	m_af[11] = 0;

	m_af[12] = 0;		m_af[13] = 0;		m_af[14] = 0;		m_af[15] = 1;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CQuat 
CSquareMatrix4::ToQuaternion()
{
	float trace = 1.0f + m_af[0] + m_af[5] + m_af[10];
	float s, x, y, z, w;

	if (trace > 1e-4f)
	{
		s = sqrt(trace) * 2.0f;
		x = ( m_af[6] - m_af[9] ) / s;
		y = ( m_af[8] - m_af[2] ) / s;
		z = ( m_af[1] - m_af[4] ) / s;
		w = 0.25f * s;
	}
	else 
	{
		if ( m_af[0] > m_af[5] && m_af[0] > m_af[10] )  {	// Column 0: 
			s  = sqrt( 1.0f + m_af[0] - m_af[5] - m_af[10] ) * 2.0f;
			x = 0.25f * s;
			y = (m_af[1] + m_af[4] ) / s;
			z = (m_af[8] + m_af[2] ) / s;
			w = (m_af[6] - m_af[9] ) / s;
		} else if ( m_af[5] > m_af[10] ) {			// Column 1: 
			s  = sqrt( 1.0f + m_af[5] - m_af[0] - m_af[10] ) * 2.0f;
			x = (m_af[1] + m_af[4] ) / s;
			y = 0.25f * s;
			z = (m_af[6] + m_af[9] ) / s;
			w = (m_af[8] - m_af[2] ) / s;
		} else {						// Column 2:
			s  = sqrt( 1.0f + m_af[10] - m_af[0] - m_af[5] ) * 2.0f;
			x = (m_af[8] + m_af[2] ) / s;
			y = (m_af[6] + m_af[9] ) / s;
			z = 0.25f * s;
			w = (m_af[1] - m_af[4] ) / s;
		}
	}

	return CQuat(x, y, z, w);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CSquareMatrix3 
CSquareMatrix4::GetRotationMatrix() const
{
	return CSquareMatrix3(m_af[0], m_af[1], m_af[2], m_af[4], m_af[5], m_af[6], m_af[8], m_af[9], m_af[10]);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CSquareMatrix4 
CSquareMatrix4::CalcRotationMatrix(const CQuat& q)
{
	return CSquareMatrix4(  1 - 2 * q.m_vA.y() * q.m_vA.y() - 2 * q.m_vA.z() * q.m_vA.z(),		2 * q.m_vA.x() * q.m_vA.y() + 2 * q.m_vA.z() * q.m_fW,			2 * q.m_vA.x() * q.m_vA.z() - 2 * q.m_vA.y() * q.m_fW,			0,
								2 * q.m_vA.x() * q.m_vA.y() - 2 * q.m_vA.z() * q.m_fW,		1 - 2 * q.m_vA.x() * q.m_vA.x() - 2 * q.m_vA.z() * q.m_vA.z(),		2 * q.m_vA.y() * q.m_vA.z() + 2 * q.m_vA.x() * q.m_fW,			0,
								2 * q.m_vA.x() * q.m_vA.z() + 2 * q.m_vA.y() * q.m_fW,			2 * q.m_vA.y() * q.m_vA.z() - 2 * q.m_vA.x() * q.m_fW,		1 - 2 * q.m_vA.x() * q.m_vA.x() - 2 * q.m_vA.y() * q.m_vA.y(),		0,
							0,																0,																0,																	1);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CSquareMatrix4 
CSquareMatrix4::CalcTranslationMatrix(const CVector<3>& vTranslation)
{
	return CSquareMatrix4(  1, 0, 0, 0,
							0, 1, 0, 0,
							0, 0, 1, 0,
							vTranslation(0), vTranslation(1), vTranslation(2), 1);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CSquareMatrix4 
CSquareMatrix4::CalcMatrix(const CQuat& q, const CVector<3>& vTranslation)
{
	return CSquareMatrix4(  1 - 2 * q.m_vA.y() * q.m_vA.y() - 2 * q.m_vA.z() * q.m_vA.z(),		2 * q.m_vA.x() * q.m_vA.y() + 2 * q.m_vA.z() * q.m_fW,			2 * q.m_vA.x() * q.m_vA.z() - 2 * q.m_vA.y() * q.m_fW,			0,
								2 * q.m_vA.x() * q.m_vA.y() - 2 * q.m_vA.z() * q.m_fW,		1 - 2 * q.m_vA.x() * q.m_vA.x() - 2 * q.m_vA.z() * q.m_vA.z(),		2 * q.m_vA.y() * q.m_vA.z() + 2 * q.m_vA.x() * q.m_fW,			0,
								2 * q.m_vA.x() * q.m_vA.z() + 2 * q.m_vA.y() * q.m_fW,			2 * q.m_vA.y() * q.m_vA.z() - 2 * q.m_vA.x() * q.m_fW,		1 - 2 * q.m_vA.x() * q.m_vA.x() - 2 * q.m_vA.y() * q.m_vA.y(),		0,
							vTranslation(0),													vTranslation(1),												vTranslation(2),													1);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CSquareMatrix4 
CSquareMatrix4::CalcMatrix(const CMatrix<3, 3>& m, const CVector<3>& vTranslation)
{
	return CSquareMatrix4(  m(0, 0),			m(0, 1),			m(0, 2),			0,
							m(1, 0),			m(1, 1),			m(1, 2),			0,
							m(2, 0),			m(2, 1),			m(2, 2),			0,
							vTranslation(0),    vTranslation(1),    vTranslation(2),	1);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// get matrix transposed
inline
CSquareMatrix4
CSquareMatrix4::GetTransposed() const
{
	return CSquareMatrix4(  m_af[0], m_af[4], m_af[8], m_af[12],
							m_af[1], m_af[5], m_af[9], m_af[13],
							m_af[2], m_af[6], m_af[10], m_af[14],
							m_af[3], m_af[7], m_af[11], m_af[15]);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// transpose matrix
inline
void
CSquareMatrix4::Transpose()
{
	for (unsigned u = 1; u < 4; ++u)
	{
		for (unsigned v = 0; v < u; ++v)
		{
			const float fSwap = m_af[u * 4 + v];
			m_af[u * 4 + v] = m_af[v * 4 + u];
			m_af[v * 4 + u] = fSwap;
		}
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// matrix multiplication mxm = mxn * nxm (random operand Rows-sizes not possible)
inline
CSquareMatrix4
CSquareMatrix4::operator*(const CMatrix<4, 4>& mMult) const
{
	CMatrix<4, 4> mResult;
	for (unsigned uResultRow = 0; uResultRow < 4; ++uResultRow)
	{
		for (unsigned uResultCol = 0; uResultCol < 4; ++uResultCol)
		{
			mResult.m_af[uResultRow * 4 + uResultCol] = 
				m_af[uResultRow * 4 + 0] * mMult.m_af[0 * 4 + uResultCol] +
				m_af[uResultRow * 4 + 1] * mMult.m_af[1 * 4 + uResultCol] +
				m_af[uResultRow * 4 + 2] * mMult.m_af[2 * 4 + uResultCol] +
				m_af[uResultRow * 4 + 3] * mMult.m_af[3 * 4 + uResultCol];
		}
	}
	return mResult;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// multiply matrix with vector (mx1 = mxn * nx1)
inline
CVector4
CSquareMatrix4::operator*(const CVector<4>& vMult) const
{
	CVector4 vResult;
	for (unsigned uResultRow = 0; uResultRow < 4; ++uResultRow)
	{
		vResult.m_af[uResultRow] = 
			m_af[uResultRow * 4 + 0] * vMult.m_af[0] +
			m_af[uResultRow * 4 + 1] * vMult.m_af[1] +
			m_af[uResultRow * 4 + 2] * vMult.m_af[2] +
			m_af[uResultRow * 4 + 3] * vMult.m_af[3];
	}
	return vResult;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// multiply matrix with vector (assume last row is 0 ... 0 1)
inline
CVector4 
CSquareMatrix4::operator^(const CVector<3>& vMult) const
{
	CVector4 vResult;
	for (unsigned uResultRow = 0; uResultRow < 4; ++uResultRow)
	{
		vResult.m_af[uResultRow] = 
			m_af[uResultRow * 4 + 0] * vMult.m_af[0] +
			m_af[uResultRow * 4 + 1] * vMult.m_af[1] +
			m_af[uResultRow * 4 + 2] * vMult.m_af[2] +
			m_af[uResultRow * 4 + 3];
	}
	return vResult;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// invert matrix
inline
void 
CSquareMatrix4::Invert()
{
	D3DXMATRIX mInverse;
	D3DXMatrixInverse(&mInverse, NULL, (D3DXMATRIX*)this);
	*this = *(CSquareMatrix4*)&mInverse;

/*
	FixElements();
	CSquareMatrix4& mIdentity = *this;
	CSquareMatrix4 mResult;
	mResult.SetIdentity();

	const float fEpsilon = 1e-7f;

	// mIdentity.m_af[ zu in Dreiecksmatrix umwandeln
	for (unsigned uCol = 0; uCol < 3; ++uCol)
	{
		// falls Element auf Hauptdiagonale = 0, Zeilen austauschen
		if (fabsf(mIdentity.m_af[uCol * 4 + uCol]) < fEpsilon)
		{
			// find one with no 0
			unsigned u = 0;
			for (u = uCol + 1; u < 4; ++u)
			{
				if (fabsf(mIdentity.m_af[u * 4 + uCol]) >= fEpsilon)
				{
					mIdentity.SwapRows(u, uCol);
					mResult.SwapRows(u, uCol);
					break;
				}
			}
			if (u == 4)
			{
				// Error ...
				assert(false);
				continue;
			}
		}

		for (unsigned uRow = uCol + 1; uRow < 4; ++uRow)
		{
			float fFact = -mIdentity.m_af[uRow * 4 + uCol] / mIdentity.m_af[uCol * 5];

			if (fFact != 0)
			{
				for (unsigned uCol2 = uCol + 1; uCol2 < 4; ++uCol2)
				{
					mIdentity.m_af[uRow * 4 + uCol2] += mIdentity.m_af[uCol * 4 + uCol2] * fFact;
				}
				for (unsigned uCol2 = 0; uCol2 < 4; ++uCol2)
				{
					mResult.m_af[uRow * 4 + uCol2] += mResult.m_af[uCol * 4 + uCol2] * fFact;
				}
			}
		}
	}

	// Dreiecksmatrix bilden (nach oben)
	for (unsigned uCol = 0; uCol < 4 - 1; ++uCol)
	{
		for (unsigned uRow = uCol + 1; uRow < 4; ++uRow)
		{
			assert(mIdentity.m_af[(3 - uCol) * 5] != 0);

			float fFact = -mIdentity.m_af[(3 - uRow) * 4 + 3 - uCol] / mIdentity.m_af[(3 - uCol) * 5];

			if (fFact != 0)
			{
				mIdentity.m_af[(3 - uRow) * 4 + 3 - uCol] += mIdentity.m_af[(3 - uCol) * 5] * fFact;

				for (unsigned uCol2 = 0; uCol2 < 4; ++uCol2)
				{
					mResult.m_af[(3 - uRow) * 4 + uCol2] += mResult.m_af[(3 - uCol) * 4 + uCol2] * fFact;
				}
			}
		}
	}


	for (unsigned uRow = 0; uRow < 4; ++uRow)
	{
		assert (mIdentity.m_af[uRow * 5] != 0);

		for (unsigned uCol = 0; uCol < 4; ++uCol)
		{
			mResult.m_af[uRow * 4 + uCol] /= mIdentity.m_af[uRow * 5];
		}
	}
	*this = mResult;
*/
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// invert matrix
inline
CSquareMatrix4
CSquareMatrix4::GetInverse() const
{
	D3DXMATRIX mInverse;
	D3DXMatrixInverse(&mInverse, NULL, (D3DXMATRIX*)this);
	return *(CSquareMatrix4*)&mInverse;
}
////-----------------------------------------------------------------------------------------------------------------------------------------
//// copy from D3DMATRIX
//#ifdef DIRECT3D_VERSION
//inline
//CSquareMatrix4::CSquareMatrix4(const D3DMATRIX& mD3D)
///*	:	m_af({	mD3D._11,	mD3D._12,	mD3D._13,	mD3D._14,
//				        mD3D._21,	mD3D._22,	mD3D._23,	mD3D._24,
//				        mD3D._31,	mD3D._32,	mD3D._33,	mD3D._34,
//				        mD3D._41,	mD3D._42,	mD3D._43,	mD3D._44})*/
//{
//	m_af[ 0] = mD3D._11;	    m_af[ 1] = mD3D._12;	    m_af[ 2] = mD3D._13;	    m_af[ 3] = mD3D._14;
//	m_af[ 4] = mD3D._21;	    m_af[ 5] = mD3D._22;	    m_af[ 6] = mD3D._23;	    m_af[ 7] = mD3D._24;
//	m_af[ 8] = mD3D._31;	    m_af[ 9] = mD3D._32;	    m_af[10] = mD3D._33;	    m_af[11] = mD3D._34;
//	m_af[12] = mD3D._41;	    m_af[13] = mD3D._42;	    m_af[14] = mD3D._43;	    m_af[15] = mD3D._44;
//}
//#endif // DIRECT3D_VERSION
////-----------------------------------------------------------------------------------------------------------------------------------------
//// assign from D3DMATRIX
//#ifdef DIRECT3D_VERSION
//inline
//const CSquareMatrix4& 
//CSquareMatrix4::operator=(const D3DMATRIX& mD3D)
//{
//	m_af[ 0] = mD3D._11;	    m_af[ 1] = mD3D._12;	    m_af[ 2] = mD3D._13;	    m_af[ 3] = mD3D._14;
//	m_af[ 4] = mD3D._21;	    m_af[ 5] = mD3D._22;	    m_af[ 6] = mD3D._23;	    m_af[ 7] = mD3D._24;
//	m_af[ 8] = mD3D._31;	    m_af[ 9] = mD3D._32;	    m_af[10] = mD3D._33;	    m_af[11] = mD3D._34;
//	m_af[12] = mD3D._41;	    m_af[13] = mD3D._42;	    m_af[14] = mD3D._43;	    m_af[15] = mD3D._44;
//	return *this;
//}
//#endif // DIRECT3D_VERSION
//
//-----------------------------------------------------------------------------------------------------------------------------------------
// cast to D3DMATRIX
//#ifdef DIRECT3D_VERSION
//inline
//CSquareMatrix4::operator D3DMATRIX()
//{
//	return *reinterpret_cast<D3DMATRIX*>(this);
//}
//#endif // DIRECT3D_VERSION

//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CVector4 
operator*(const CVector<3>& v, const CMatrix<4, 4>& m)
{
	return CVector4(
		v.m_af[0] * m.m_af[0 + 4 * 0] + v.m_af[1] * m.m_af[0 + 4 * 1] + v.m_af[2] * m.m_af[0 + 4 * 2] + m.m_af[0 + 4 * 3],
		v.m_af[0] * m.m_af[1 + 4 * 0] + v.m_af[1] * m.m_af[1 + 4 * 1] + v.m_af[2] * m.m_af[1 + 4 * 2] + m.m_af[1 + 4 * 3],
		v.m_af[0] * m.m_af[2 + 4 * 0] + v.m_af[1] * m.m_af[2 + 4 * 1] + v.m_af[2] * m.m_af[2 + 4 * 2] + m.m_af[2 + 4 * 3],
		v.m_af[0] * m.m_af[3 + 4 * 0] + v.m_af[1] * m.m_af[3 + 4 * 1] + v.m_af[2] * m.m_af[3 + 4 * 2] + m.m_af[3 + 4 * 3]);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CVector3
operator^(const CVector<3>& v, const CMatrix<4, 4>& m)
{
	return CVector3(
		v.m_af[0] * m.m_af[0 + 4 * 0] + v.m_af[1] * m.m_af[0 + 4 * 1] + v.m_af[2] * m.m_af[0 + 4 * 2] + m.m_af[0 + 4 * 3],
		v.m_af[0] * m.m_af[1 + 4 * 0] + v.m_af[1] * m.m_af[1 + 4 * 1] + v.m_af[2] * m.m_af[1 + 4 * 2] + m.m_af[1 + 4 * 3],
		v.m_af[0] * m.m_af[2 + 4 * 0] + v.m_af[1] * m.m_af[2 + 4 * 1] + v.m_af[2] * m.m_af[2 + 4 * 2] + m.m_af[2 + 4 * 3]);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CVector4 
operator*(const CVector<4>& v, const CMatrix<4, 4>& m)
{
	return CVector4(
		v.m_af[0] * m.m_af[0 + 4 * 0] + v.m_af[1] * m.m_af[0 + 4 * 1] + v.m_af[2] * m.m_af[0 + 4 * 2] + v.m_af[3] * m.m_af[0 + 4 * 3],
		v.m_af[0] * m.m_af[1 + 4 * 0] + v.m_af[1] * m.m_af[1 + 4 * 1] + v.m_af[2] * m.m_af[1 + 4 * 2] + v.m_af[3] * m.m_af[1 + 4 * 3],
		v.m_af[0] * m.m_af[2 + 4 * 0] + v.m_af[1] * m.m_af[2 + 4 * 1] + v.m_af[2] * m.m_af[2 + 4 * 2] + v.m_af[3] * m.m_af[2 + 4 * 3],
		v.m_af[0] * m.m_af[3 + 4 * 0] + v.m_af[1] * m.m_af[3 + 4 * 1] + v.m_af[2] * m.m_af[3 + 4 * 2] + v.m_af[3] * m.m_af[3 + 4 * 3]);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CVector3 
operator*(const CVector<3>& v, const CMatrix<3, 3>& m)
{
	return CVector3(
		v.m_af[0] * m.m_af[0 + 3 * 0] + v.m_af[1] * m.m_af[0 + 3 * 1] + v.m_af[2] * m.m_af[0 + 3 * 2],
		v.m_af[0] * m.m_af[1 + 3 * 0] + v.m_af[1] * m.m_af[1 + 3 * 1] + v.m_af[2] * m.m_af[1 + 3 * 2],
		v.m_af[0] * m.m_af[2 + 3 * 0] + v.m_af[1] * m.m_af[2 + 3 * 1] + v.m_af[2] * m.m_af[2 + 3 * 2]);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------------------------------------------------------------
