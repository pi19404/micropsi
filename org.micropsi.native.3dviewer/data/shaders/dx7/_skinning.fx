//-----------------------------------------------------------------------------------------------------------------------------------------
static const int	SKINNING_MATRIX_PALETTE_SIZE = 26;

//-----------------------------------------------------------------------------------------------------------------------------------------
// CalcSkinning - Funktion für den VertexShader; gibt die geblendete Position&Normale im Worldspace zurück
void
CalcSkinning(
	inout float3 vPos, 
	inout float3 vNrm, 
	in float4 vBlendWeights, 
	in float4 vBlendIndices, 
	uniform int iNumBones,
	uniform float4x3 amWorldMatrixArray[SKINNING_MATRIX_PALETTE_SIZE])
{
	// work around, wegen fehlenden support für UBYTE4 auf Geforce3
	int4 vIndexVector = D3DCOLORtoUBYTE4(vBlendIndices);


	// Vektoren zu Arrays casten (für den LookUp)
	float afBlendWeightsArray[4]	= (float[4])vBlendWeights;
	int   aiIndexArray[4]			= (int[4])vIndexVector;

	
	// gewichtetes Addieren der verschiedenen Transformationen
	float	fLastWeight = 1.0f;
	float3	vWorldPos = 0; 
	float3	vWorldNrm = 0;
	
	for (int iBone = 0; iBone < iNumBones - 1; iBone++)
	{
		vWorldPos += mul(float4(vPos, 1),	amWorldMatrixArray[aiIndexArray[iBone]]) * afBlendWeightsArray[iBone];
		vWorldNrm += mul(float4(vNrm, 0),	amWorldMatrixArray[aiIndexArray[iBone]]) * afBlendWeightsArray[iBone];

		fLastWeight -= afBlendWeightsArray[iBone];
	}

	// Addition der letzten Transformation
	vWorldPos += mul(float4(vPos, 1),	amWorldMatrixArray[aiIndexArray[iNumBones - 1]]) * fLastWeight;
	vWorldNrm += mul(float4(vNrm, 0),	amWorldMatrixArray[aiIndexArray[iNumBones - 1]]) * fLastWeight; 
	
	
	// in/out-Werte setzen
	vPos = vWorldPos;
	vNrm = vWorldNrm;
}
//-----------------------------------------------------------------------------------------------------------------------------------------

technique t0 {}	// dummy technique
