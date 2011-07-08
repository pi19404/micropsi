#pragma once

#ifndef E42_TRIANGLECOLLECTOR_H_INCLUDED
#define E42_TRIANGLECOLLECTOR_H_INCLUDED

#include "e42/stdinc.h"

#include "e42/core/EngineController.h"
#include "e42/core/DeviceStateMgr.h"
#include "e42/core/EffectShader.h"

/*
CTriangleCollector
    
    Idee:
        Die Klasse dient dem Aufbau einer Geometrie aus Vertices und Indices. Gekapselt werden dabei die
        Index- und Vertexbuffer sowie die Lock()-/Unlock()-Aufrufe für diese. Weiterhin ist es möglich,
        falls dynamische Vertexbuffer verwendet werden, automatisch ein Rendern auszulösen, sollten einer
        der Buffer voll sein.

    Verwendung:
        Zum Hinzufügen neuer Vertices und Indices wird ein Schreibfenster für die Buffer verwendet. Das 
        Schreibfenster kann mittels der Funktion SetWriteWindow auf die Größe der Einträge gesetzt werden,
        die hinzugefügt werden sollen. Das zuletzt aktuelle Fenster wird dabei automatisch geschlossen. 
        Nachdem ein Schreibfenster angelegt wurde, kann mit der Funktion Vertex(idx) auf einen Vertex im 
        VertexBuffer-Schreibfenster zugegriffen werden. Der übergebene Index ist relativ zum Anfang des
        Fensters. Mit der Funktion AddTriangle kann ein Dreieck hinzugefügt werden. Die übergebenen Vertex-
        Indices beziehen sich ebenfalls auf den Anfang des Schreibfensters.
        Sollte der Platz für das angeforderte Schreibfenster nicht mehr vorhanden sein, werden die Buffer
        automatisch geflusht (gerendert).

    Beispiel:
            TriangleCollector<..., ...> tc(...);
            tc.SetVertexBuffer(...);
            tc.SetIndexBuffer(...);
            tc.Lock();

            
            tc.SetWriteWindow(4, 6);

            tc.Vertex(0).p = CVec3(0, 0, 0);
            tc.Vertex(1).p = CVec3(0, 1, 0);
            tc.Vertex(2).p = CVec3(1, 0, 0);
            tc.Vertex(3).p = CVec3(1, 1, 0);

            tc.AddTriangle(0, 1, 2);
            tc.AddTriangle(2, 2, 2);


            tc.SetWritewindow(3, 3);

            tc.Vertex(0).p = CVec3(1, 0, 0);
            tc.Vertex(1).p = CVec3(0, 0, 1);
            tc.Vertex(2).p = CVec3(1, 0, 1);

            tc.AddTriangle(0, 1, 2);


            tc.Unlock();

            tc.Flush();

    Implementation:
        Die Klasse wurde als Template implementiert, damit unterschiedliche VertexTypen/IndexTypen verwendet 
        werden können, ohne dass jedesmal ein Cast stattfinden muss, wenn man auf die Vektoren/Indices 
        zugreift. Die RenderBuffers()-Funktion ist virtuell, damit sie gegebenenfalls von einer abgeleiteten
        Klasse überschrieben werden kann.
        Das Schreibfenster wird durch Head- und Tails verwaltet, und verschiebt sich jeweils nach hinten,
        wenn eine neues Schreibfenster gesetzt wird.
*/

template<typename VertexType, typename IndexType>
class CTriangleCollector
{
private:
    CEngineController*          m_pxEngineController;


    TVertexBufferHandle         m_hndVertexBuffer;          // verwendeter Vertexbuffer
    int                         m_iMaxVertices;             // maximale Anzahl der Vertices im Buffer

    VertexType*                 m_pxVertices;               // VertexArray (falls Buffer gelockt ist)
    int                         m_iVertexTail;              // markiert das untere Ende des Vertex-Schreibfensters
    int                         m_iVertexHead;              // markiert das obere Ende des Vertex-Schreibfensters
    
    
    TIndexBufferHandle          m_hndIndexBuffer;           // verwendeter Indexbuffer
    int                         m_iMaxIndices;              // maximale Anzahl der Indices im Buffer

    IndexType*                  m_pxIndices;                // IndexArray (falls Buffer gelockt ist)
    int                         m_iIndexTail;               // markiert das untere Ende des Index-Schreibfensters
    int                         m_iIndexHead;               // markiert das obere Ende des Index-Schreibfensters
    
    int                         m_iCurrentTriangleOffset;   // Index des Dreiecks, das seit dem letzten SetWindow geaddet wurde


    TEffectHandle               m_hndEffect;                // Effekt, der zum Rendern verwendet werden soll

    DWORD                       m_dwVertexFVF;              // FVF der Vektoren
    TVertexDeclarationHandle    m_hndVertexDeclaration;     // VertexDeclaration (überschreibt FVF falls vorhanden)

    bool                        m_bLockState;               // true, falls Buffer momentan gelockt sind

    virtual void RenderBuffers();                           // rendert die Buffer

public:
    CTriangleCollector(CEngineController* pxEngineController = NULL);
    ~CTriangleCollector();

    void        SetVertexBuffer(TVertexBufferHandle hndVB, int iMaxVertices);
    void        SetIndexBuffer(TIndexBufferHandle hndIB, int iMaxIndices);
    void        SetEffect(TEffectHandle hndFX);
    void        SetVertexFVF(DWORD dwVertexFVF);
    void        SetVertexDeclaration(TVertexDeclarationHandle hndVD);
    

    void        Lock();                                     // manuelles Locken
    void        Unlock();                                   // manuelles Unlock (damit die Buffer nicht die ganze Zeit gelockt bleibt)

    void        Flush();                                    // Flushen -> Rendert den Inhalt der Buffer

    void        SetWriteWindow(int iVertices, int iIndices); // setzt ein neues Schreibfenster

    VertexType& Vertex(int iOffset);                        // Zugriff auf die Vertices im Schreibfenster
    void        AddTriangle(int iW0, int iW1, int iW2);     // fügt ein neues Dreieck zum Schreibfenster hinzu
};

#include "TriangleCollector.inl"

#endif // E42_TRIANGLECOLLECTOR_H_INCLUDED
