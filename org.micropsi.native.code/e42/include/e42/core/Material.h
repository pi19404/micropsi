#pragma once

#ifndef E42_MATERIAL_H_INCLUDED
#define E42_MATERIAL_H_INCLUDED

#include "e42/stdinc.h"
#include "e42/core/ResourceHandles.h"
#include "e42/core/ShaderConstants.h"
#include "baselib/geometry/CVector.h"
#include "baselib/geometry/Matrix.h"
#include <d3dx9mesh.h>

#include <string>

class CEngineController;
class CD3DXEffectInstance;
class CD3DXEffectDefault;
class CD3DXMaterial;

class CMaterial
{
private:
	class CEffectParameter
	{
	public:

		class CEffectParameterValue
		{
		private:
			CEffectParameterValue& operator=(const CEffectParameterValue& rxEPV);

			union
			{
				int				m_iValue;							///< Wert des Parameters (bei Basisdatentypen bis 4 Byte)
				void*			m_pValue;							///< Pointer auf den Wert des Parameters (bei Basisdatentypen, die nicht inplace gespeichert werden können))
			};

			size_t			m_iSize;								///< Größe des Wertes in Byte

			TTextureHandle	m_hTexture;							///< Wert des Parameters, falls es sich um eine Textur handelt

			#define MAX_INPLACE_VALUESIZE (sizeof(m_iValue))


		public:
			CEffectParameterValue();
			~CEffectParameterValue();

			void SetSize(size_t iNewValueSize);
			void Set_Void(const void* pValue, size_t iValueSize);

			size_t GetSize_Void() const;
			const void* Get_Void() const;
			void* Get_Void();


			void Set_Texture(const TTextureHandle& hndTexture);
			TTextureHandle Get_Texture() const;
		};


		D3DXHANDLE				m_hParameter;						///< Handle des Parameters für den Effekt (ermöglicht schnellen Zugriff)
		D3DXPARAMETER_TYPE		m_eParameterType;					///< Typ des Parameters (float, int, string)
		std::string				m_sParameterName;					///< Name des Parameters
		int						m_iParameterSize;					///< Größe des Parameters (muss nicht zwingend mit der Größe des aktuellen Wertes übereinstimmen)                                  
		CEffectParameterValue	m_xParameterValue;					///< aktueller Wert
        
		CEffectParameter();
		~CEffectParameter();
		CEffectParameter(const CEffectParameter& rxEffectParameter);
		CEffectParameter& operator=(const CEffectParameter& rxEffectParameter);

		bool ParameterTypeIsTexture() const;
	};

	CDynArray<CEffectParameter, 3>	m_axEffectParameters;			///< Array für die generischen Parameter

	int FindParameterIdx(const std::string& sParameterName);		///< sucht einen Parameter bzw. gibt -1 zurück, falls dieser nicht gefunden wurde
	int GetParameterIdx(const std::string& sParameterName);			///< sucht einen Parameter und fügt ihn hinzu, wenn er im ID3DXEffect definiert wird


	CEngineController*	m_pxEngineController;

	TEffectHandle		m_hEffect;									///< Handle für den Effekt

	D3DXHANDLE			m_hParameterBlock;							///< gecashtet ParameterBlock
	void				InvalidateParameterBlock();					///< gibt den aktuellen Parameterblock frei


	void				ApplyEffectParameters();					///< setzt die gespeicherten Parameter auf dem Effekt


	bool				IsTopLevelParameter(const std::string& sParameterName) const;
	bool				ParameterContextIsObject(const std::string& sParameterName) const;	///< prüft ob der Parameter von dem Material gesetzt werden soll
	bool				IsTextureParameter(const CD3DXEffectDefault& rxDefault) const;


public:

	CMaterial();
	~CMaterial();
	CMaterial(const CMaterial& rxMaterial);
	CMaterial& operator=(const CMaterial& rxMaterial);

	TEffectHandle GetEffect() const;

	void UpdateEffect();																	///< setzt die Parameter auf dem Effekt
	void Init(const char* pcEffectFilename, CEngineController* pxEngineController);			///< initialisiert das Material mittels EffectInstances


	void AddParameters(const CD3DXEffectDefault* pxDefaults, int iNumDefaults);


	void SetParameter(const std::string& sParameterName, void* pValue, size_t iValueSize);	///< weist dem Parameter einen neuen Wert zu
	void SetParameterF(const std::string& sParameterName, float fValue);
	void SetParameterI(const std::string& sParameterName, int iValue);
	void SetParameterV2(const std::string& sParameterName, const CVec2& vValue);
	void SetParameterV3(const std::string& sParameterName, const CVec3& vValue);
	void SetParameterV4(const std::string& sParameterName, const CVec4& vValue);
	void SetParameterM44(const std::string& sParameterName, const CMat4S& mValue);
	void SetTexture(const std::string& sParameterName, TTextureHandle& hndTexture);

	bool GetParameterS(const std::string& sParameterName, std::string* pValueOut);


	// shortcuts für SetTexture, (benutzt standard Shaderkonstanten)
	void SetDiffuseMap(TTextureHandle& hndDiffuseMap);
	void SetSpecularMap(TTextureHandle& hndSpecularMap);
	void SetNormalMap(TTextureHandle& hndNormalMap);
	void SetBumpMap(TTextureHandle& hndBumpMap);
	void SetDetailMap(TTextureHandle& hndDetailMap);
	void SetEnvironmentMap(TTextureHandle& hndEnvironmentMap);
	void SetShadowMap(TTextureHandle& hndShadowMap);
	void SetLightMap(TTextureHandle& hndLightMap);
	
	
	int GetMaterialSortingID() const;


#ifdef _DEBUG
	std::string			_m_sEffectFilename;							///< Name des FX-Files
#endif // _DEBUG
};

#include "Material.inl"

#endif // E42_MATERIAL_H_INCLUDED