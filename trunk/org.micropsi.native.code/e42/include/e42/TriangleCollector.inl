//-----------------------------------------------------------------------------------------------------------------------------------------
// Konstruktor
template<typename VertexType, typename IndexType>
CTriangleCollector<VertexType, IndexType>::CTriangleCollector(CEngineController* pxEngineController)
:   m_pxEngineController    (pxEngineController),

	m_iMaxVertices			(0),
	m_pxVertices			(NULL),
	m_iVertexTail			(0),
	m_iVertexHead			(0),

	m_iMaxIndices			(0),
	m_pxIndices				(NULL),
	m_iIndexTail			(0),
	m_iIndexHead			(0),

	m_iCurrentTriangleOffset(0),

	m_dwVertexFVF			(0),
	
	m_bLockState			(false)
{
	if (m_pxEngineController == NULL)
		m_pxEngineController = &CEngineController::Get();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// Destruktor
template<typename VertexType, typename IndexType>
CTriangleCollector<VertexType, IndexType>::~CTriangleCollector()
{
	assert(m_bLockState == false);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// rendert die Buffer
template<typename VertexType, typename IndexType>
void
CTriangleCollector<VertexType, IndexType>::RenderBuffers()
{
	assert(m_dwVertexFVF != 0 || m_hndVertexDeclaration.GetPtr() != 0);
	assert(m_hndEffect.GetPtr() != NULL);
	assert(m_hndVertexBuffer.GetPtr() != NULL);
	assert(m_hndIndexBuffer.GetPtr() != NULL);


	if (m_iIndexHead > 0)
	{
		IDirect3DDevice9* pd3dDevice = m_pxEngineController->GetDevice();
		CDeviceStateMgr* pd3dDeviceStateMgr = m_pxEngineController->GetDeviceStateMgr();
		CEffectShader* pEffect = m_hndEffect.GetPtr();


		pd3dDeviceStateMgr->SetIndices(m_hndIndexBuffer.GetPtr());
		pd3dDeviceStateMgr->SetStreamSource(0, m_hndVertexBuffer.GetPtr(), 0, sizeof(VertexType));

		if (m_hndVertexDeclaration.GetPtr())
		{
			pd3dDeviceStateMgr->SetVertexDeclaration(m_hndVertexDeclaration.GetPtr());
		}
		else
		{
			pd3dDeviceStateMgr->SetFVF(m_dwVertexFVF);
		}


		// Effekt starten
		unsigned int uiNumPasses = 0;
		pEffect->Begin(&uiNumPasses, D3DXFX_DONOTSAVESTATE);
		unsigned int uiNumRenderPasses = uiNumPasses;
	    
		if (uiNumPasses > 1 &&
			pEffect->IsRestorePass(uiNumPasses - 1))
		{
			uiNumRenderPasses--;
		}


		// Rendern
		for (UINT iPass = 0; iPass < uiNumRenderPasses; iPass++)
		{
			pEffect->BeginPass(iPass);
			pd3dDevice->DrawIndexedPrimitive(D3DPT_TRIANGLELIST, 0, 0, m_iVertexHead, 0, m_iIndexHead / 3);
			pEffect->EndPass();
		}


		// alten State wiederherstellen?
		if (uiNumPasses > uiNumRenderPasses)
		{
			pEffect->BeginPass(uiNumPasses - 1);
			pEffect->EndPass();
		}

		pEffect->End();
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// setzt den Vertexbuffer
template<typename VertexType, typename IndexType>
void
CTriangleCollector<VertexType, IndexType>::SetVertexBuffer(TVertexBufferHandle hndVB, int iMaxVertices)
{
	m_hndVertexBuffer = hndVB;
	m_iMaxVertices = iMaxVertices;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// setzt den Indexbuffer
template<typename VertexType, typename IndexType>
void
CTriangleCollector<VertexType, IndexType>::SetIndexBuffer(TIndexBufferHandle hndIB, int iMaxIndices)
{
	m_hndIndexBuffer = hndIB;
	m_iMaxIndices = iMaxIndices;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// setzt den zu verwendenden Effekt
template<typename VertexType, typename IndexType>
void
CTriangleCollector<VertexType, IndexType>::SetEffect(TEffectHandle hndFX)
{
	m_hndEffect = hndFX;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// setzt das zu verwendende Vektorformat
template<typename VertexType, typename IndexType>
void 
CTriangleCollector<VertexType, IndexType>::SetVertexFVF(DWORD dwVertexFVF)
{
	m_dwVertexFVF = dwVertexFVF;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// setzt die zu verwendenden VertexDeclaration
template<typename VertexType, typename IndexType>
void
CTriangleCollector<VertexType, IndexType>::SetVertexDeclaration(TVertexDeclarationHandle hndVD)
{
	m_hndVertexDeclaration = hndVD;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// Lockt die Buffer
template<typename VertexType, typename IndexType>
void
CTriangleCollector<VertexType, IndexType>::Lock()
{
	assert(m_bLockState == false);

	HRESULT hr;

	hr = m_hndVertexBuffer->Lock(0, sizeof(VertexType) * m_iMaxVertices, (void**)&m_pxVertices, D3DLOCK_DISCARD);
	assert(SUCCEEDED(hr));

	hr = m_hndIndexBuffer->Lock(0, sizeof(IndexType) * m_iMaxIndices, (void**)&m_pxIndices, D3DLOCK_DISCARD);
	assert(SUCCEEDED(hr));

	m_bLockState = true;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// Unlockt die Buffer
template<typename VertexType, typename IndexType>
void
CTriangleCollector<VertexType, IndexType>::Unlock()
{
	assert(m_iIndexTail + m_iCurrentTriangleOffset == m_iIndexHead);
	m_iIndexTail = m_iIndexHead;
	m_iVertexTail = m_iVertexHead;

	assert(m_bLockState == true);

	HRESULT hr;

	hr = m_hndVertexBuffer->Unlock();
	assert(SUCCEEDED(hr));
	m_pxVertices = NULL;

	hr = m_hndIndexBuffer->Unlock();
	assert(SUCCEEDED(hr));
	m_pxIndices = NULL;

	m_bLockState = false;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// Flusht die Buffer (rendert und setzt die Schreibmarken zurück)
template<typename VertexType, typename IndexType>
void
CTriangleCollector<VertexType, IndexType>::Flush()
{
	if (m_iVertexHead > 0 &&
		m_iIndexHead > 0)
	{
		bool bCurrentLockState = m_bLockState;

		if (bCurrentLockState == true)
		{
			Unlock();
		}

		RenderBuffers();

		m_iVertexHead = 0;
		m_iVertexTail = 0;

		m_iIndexHead = 0;
		m_iIndexTail = 0;

		if (bCurrentLockState == true)
		{
			Lock();
		}
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// setzt ein neues Schreibfenster (und flusht, falls nötig)
template<typename VertexType, typename IndexType>
void
CTriangleCollector<VertexType, IndexType>::SetWriteWindow(int iNumVertices, int iNumIndices)
{
	assert(m_iIndexTail + m_iCurrentTriangleOffset == m_iIndexHead);
	assert(iNumVertices >= 3);
	assert(iNumIndices >= 3);
	assert(iNumIndices % 3 == 0);

	if (m_iVertexHead + iNumVertices > m_iMaxVertices ||
		m_iIndexHead + iNumIndices > m_iMaxIndices)
	{
		Flush();
	}

	assert (m_iVertexHead + iNumVertices <= m_iMaxVertices &&
			m_iIndexHead + iNumIndices <= m_iMaxIndices);  // sonst nicht genügend Platz im Buffer

	if (m_bLockState == false)
	{
		Lock();
	}

	m_iVertexTail = m_iVertexHead;
	m_iIndexTail = m_iIndexHead;

	m_iVertexHead += iNumVertices;
	m_iIndexHead += iNumIndices;

	m_iCurrentTriangleOffset = 0;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// (Schreib-)Zugriff auf einen Vertex
template<typename VertexType, typename IndexType>
VertexType&
CTriangleCollector<VertexType, IndexType>::Vertex(int iOffset)
{
	assert(m_bLockState == true);

	assert(iOffset >= 0);
	assert(m_iVertexTail + iOffset < m_iVertexHead);

	return *(m_pxVertices + m_iVertexTail + iOffset);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// schreibt ein neues Dreieck in den Buffer
template<typename VertexType, typename IndexType>
void
CTriangleCollector<VertexType, IndexType>::AddTriangle(int iW0, int iW1, int iW2)
{
	assert(m_bLockState == true);

	// checken, ob Vertex-Indices im Fenster liegen
	assert(iW0 >= 0 && iW1 >= 0 && iW2 >= 0);
	assert(m_iVertexTail + iW0 < m_iVertexHead);
	assert(m_iVertexTail + iW1 < m_iVertexHead);
	assert(m_iVertexTail + iW2 < m_iVertexHead);

	// checken, ob noch genug Platz ist um ein Dreieck zu den Vertices hinzuzufügen
	assert(m_iIndexTail + m_iCurrentTriangleOffset + 2 < m_iIndexHead);

	// Dreieck schreiben
	m_pxIndices[m_iIndexTail + m_iCurrentTriangleOffset + 0] = m_iVertexTail + iW0;
	m_pxIndices[m_iIndexTail + m_iCurrentTriangleOffset + 1] = m_iVertexTail + iW1;
	m_pxIndices[m_iIndexTail + m_iCurrentTriangleOffset + 2] = m_iVertexTail + iW2;

	m_iCurrentTriangleOffset += 3;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
