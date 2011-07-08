#ifndef E42_SCENEGRAPH_ITERATOR_H_INCLUDED
#define E42_SCENEGRAPH_ITERATOR_H_INCLUDED

class CD3DXFrame;
class CD3DXMeshContainer;

/*
	SceneGraphIterator:
		kann �ber alle Frames/MeshContainer einer (Sub-)FrameHierarche iterieren:

		Beispiel:

			CSceneGraphIterator xIterator(hndModel->GetRootFrame());
			while (pxMeshContainer = xIterator.GetNextMeshContainer())
			{
				pxMeshContainer->DoBlah()
			}
*/

class CSceneGraphIterator
{
private:
	CD3DXFrame*			m_pxStartFrame;

	CD3DXFrame*			m_pxCurrentFrame;
	CD3DXMeshContainer*	m_pxCurrentMeshContainer;

public:
	CSceneGraphIterator(CD3DXFrame* pxStartFrame);
	~CSceneGraphIterator();


	/// gibt aktuellen Frame zur�ck (nach dem Start noch NULL)
	CD3DXFrame*			GetCurrentFrame() const;

	/// w�hlt den n�chsten Frame aus und gibt ihn zur�ck 
	CD3DXFrame*			GetNextFrame();


	/// gibt aktuellen MeshContainer zur�ck (nach dem Start noch NULL)
	CD3DXMeshContainer* GetCurrentMeshContainer() const;

	/// w�hlt den n�chsten MeshContainer aus und gibt ihn zur�ck
	CD3DXMeshContainer* GetNextMeshContainer();
};

#endif // E42_SCENEGRAPH_ITERATOR_H_INCLUDED