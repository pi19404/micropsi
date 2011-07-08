/*******************************************************************************
 Camera.h - die Klasse CCamera ist eine simple Kameraklasse, die vor allem dem
    Setup der View- und Projection-Matrix, sowie der Berechnung der 
    ClippingPlanes des ViewFrustums dient.
*******************************************************************************/
#pragma once

#ifndef CAMERA_H_INCLUDED
#define CAMERA_H_INCLUDED

#include "e42/stdinc.h"

#include "baselib/geometry/CVector.h"
#include "baselib/geometry/Matrix.h"
#include "baselib/geometry/BoundingSphere.h"

#include "e42/core/ViewFrustum.h"

#include "e42/core/RenderContext.h"

class CCamera
{
protected:

	bool	m_bLeftHandedWorldCoordinateSystem;


	// View-Matrix-Daten
	CVec3   m_vLookAtDir;					///< Orientierung
	CVec3   m_vUpVector;					///< nicht-orthogonalisierter UpVector

	CVec3   m_vPos;


	// Projection-Matrix-Daten
	float   m_fFarPlaneDistance;
	float   m_fNearPlaneDistance;

	bool    m_bPerspective;
	float   m_fFieldOfViewHeight;			///< Öffnungswinkel der Kamera (bei Perspektive), Höhe des Sichtquaders (bei orthogonaler Kamera)
	float   m_fAspectRatio;


	// Kamera-Matrizen
	CMat4S  m_matProjection;
	CMat4S  m_matProjectionInverse;

	CMat4S  m_matView;
	CMat4S  m_matViewInverse;

	CMat4S  m_matViewProjection;
	CMat4S  m_matViewProjectionInverse;

	CViewFrustum	m_ViewFrustum;


	// Berechnungsfunktionen für die Matrizen
	void SetupCombinedMatrices();
	void SetupViewMatrix();
	void SetupProjectionMatrix();


	CVec3 CalcOrthogonalUpVec() const;			///< berechnet einen UpVector, der senkrecht auf der Orientation steht
	CVec3 CalcOrthogonalUpVec(CVec3& vRightVec) const;


public:

	// Konstruktor/Destruktor
	CCamera();
	~CCamera();

	

	// Projection-Matrix-Daten
	void SetFieldOfView(float fHeight, float fAspectRatio, bool bPerspective);
	
	void SetAspect(float fAspectRatio);
	float GetAspect() const;

	void SetFieldOfViewHeight(float fHeight);
	float GetFieldOfViewHeight() const;

	void SetPerspective(bool bPerspective);
	bool GetPerspective() const;

	void SetFarPlaneDistance(float fDist);
	float GetFarPlaneDistance() const;

	void SetNearPlaneDistance(float fDist);
	float GetNearPlaneDistance() const;



	// View-Matrix-Daten
	void SetPos(const CVec3& vPos);
	const CVec3& GetPos() const;

	void SetOrientation(const CVec3& vDir);
	const CVec3& GetOrientation() const;

	void SetOrientationByLookAtPoint(const CVec3& vPoint);

	void SetUpVec(const CVec3& vUpVec);
	const CVec3& GetUpVec() const;

	CVec3 GetRightVec() const;

	void SetLeftHandedWorldCoordinateSystem(bool bRHCS);
	bool GetLeftHandedWorldCoordinateSystem() const;



	// Movement
	void MoveRight(float fDistance);
	void MoveUp(float fDistance);			///< bewegt die Kamera nach oben (aus Sicht der Kamera)
	void MoveForward(float fDistance);

	// Movement im Weltkoordinatensystem
	void MoveWorldUp(float fDistance);		///< bewegt die Kamera nach oben (aus Sicht der Welt)


	// Rotation um das Kamerakoordinatensystem
	void RotateUp(float fAngle);			///< Rotation um X
	void RotateRight(float fAngle);			///< Rotation um Y
	void RotateCW(float fAngle);			///< Rotation um Z (Rollen) btw: wenn sich die Kamera CW dreht, dreht sich das Bild CCW

	// Rotation um Weltkoordinatensystem
	void RotateWorldY(float fAngle);



	// Matrizen
	const CMat4S& GetViewMatrix() const;
	const CMat4S& GetViewInverseMatrix() const;

	const CMat4S& GetProjectionMatrix() const;
	const CMat4S& GetProjectionInverseMatrix() const;

	const CMat4S& GetViewProjectionMatrix() const;
	const CMat4S& GetViewProjectionInverseMatrix() const;


	const CViewFrustum& GetViewFrustum() const;									///< macht ViewFrustumUpdate, so dass es im !ModelSpace! verwendet werden kann (ViewFrustum wird ungültig, wenn WorldTransform geändert wird!)


	void SetupMatrices();														///< berechnet View- & ProjectionMatrix nach aktuellen Settings

	virtual void UpdateViewFrustum();											///< aktualisiert das ViewFrustum

	void SetupRenderContext(const TRenderContextPtr& pxRenderContext) const;	///< initialisiert die Matrizen im RenderContext
};

#include "e42/Camera.inl"

#endif // CAMERA_H_INCLUDED