#ifndef E42_SCENEGRAPH_ITERATOR_H_INCLUDED
#define E42_SCENEGRAPH_ITERATOR_H_INCLUDED

class CD3DXFrame;
class CD3DXMeshContainer;

/*
	SceneGraphIterator:
		kann über alle Frames/MeshContainer einer (Sub-)FrameHierarche iterieren:

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


	/// gibt aktuellen Frame zurück (nach dem Start noch NULL)
	CD3DXFrame*			GetCurrentFrame() const;

	/// wählt den nächsten Frame aus und gibt ihn zurück 
	CD3DXFrame*			GetNextFrame();


	/// gibt aktuellen MeshContainer zurück (nach dem Start noch NULL)
	CD3DXMeshContainer* GetCurrentMeshContainer() const;

	/// wählt den nächsten MeshContainer aus und gibt ihn zurück
	CD3DXMeshContainer* GetNextMeshContainer();
};

#endif // E42_SCENEGRAPH_ITERATOR_H_INCLUDED