//--------------------------------------------------------------------------//
//-- macros for equal element accesses -------------------------------------//
//--------------------------------------------------------------------------//

#define ADDDEFINITION_ELEMENTACCESS_X(idx)										\
/*--------------------------------------------------------------------------*/	\
/* access x-component const */													\
	const float x() const														\
	{																			\
		return m_af[idx];														\
	};																			\
																				\
/*--------------------------------------------------------------------------*/	\
/* access x-component ref */													\
	float& x()																	\
	{																			\
		return m_af[idx];														\
	};																			

#define ADDDEFINITION_ELEMENTACCESS_Y(idx)										\
/*--------------------------------------------------------------------------*/	\
/* access y-component const */													\
	const float y() const														\
	{																			\
		return m_af[idx];														\
	};																			\
																				\
/*--------------------------------------------------------------------------*/	\
/* access y-component ref */													\
	float& y()																	\
	{																			\
		return m_af[idx];														\
	};																			

#define ADDDEFINITION_ELEMENTACCESS_Z(idx)										\
/*--------------------------------------------------------------------------*/	\
/* access z-component const */													\
	const float z() const														\
	{																			\
		return m_af[idx];														\
	};																			\
																				\
/*--------------------------------------------------------------------------*/	\
/* access z-component ref */													\
	float& z()																	\
	{																			\
		return m_af[idx];														\
	};																			

#define ADDDEFINITION_ELEMENTACCESS_W(idx)										\
/*--------------------------------------------------------------------------*/	\
/* access w-component const */													\
	const float w() const														\
	{																			\
		return m_af[idx];														\
	};																			\
																				\
/*--------------------------------------------------------------------------*/	\
/* access w-component ref */													\
	float& w()																	\
	{																			\
		return m_af[idx];														\
	};		

#define ADDDEFINITION_ELEMENTACCESS_U(idx)										\
/*--------------------------------------------------------------------------*/	\
/* access u-component const */													\
	const float u() const														\
	{																			\
		return m_af[idx];														\
	};																			\
																				\
/*--------------------------------------------------------------------------*/	\
/* access u-component ref */													\
	float& u()																	\
	{																			\
		return m_af[idx];														\
	};																			

#define ADDDEFINITION_ELEMENTACCESS_V(idx)										\
/*--------------------------------------------------------------------------*/	\
/* access v-component const */													\
	const float v() const														\
	{																			\
		return m_af[idx];														\
	};																			\
																				\
/*--------------------------------------------------------------------------*/	\
/* access v-component ref */													\
	float& v()																	\
	{																			\
		return m_af[idx];														\
	};																			

#define ADDDEFINITION_ELEMENTACCESS_RHW(idx)									\
/*--------------------------------------------------------------------------*/	\
/* access v-component const */													\
	const float rhw() const														\
	{																			\
		return m_af[idx];														\
	};																			\
																				\
/*--------------------------------------------------------------------------*/	\
/* access v-component ref */													\
	float& rhw()																\
	{																			\
		return m_af[idx];														\
	};																			

#define ADDDEFINITION_ELEMENTACCESS_ANG(idx)									\
/*--------------------------------------------------------------------------*/	\
/* access v-component const */													\
	const float ang() const														\
	{																			\
		return m_af[idx];														\
	};																			\
																				\
/*--------------------------------------------------------------------------*/	\
/* access v-component ref */													\
	float& ang()																\
	{																			\
		return m_af[idx];														\
	};																			
