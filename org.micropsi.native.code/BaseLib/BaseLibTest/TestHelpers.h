extern int g_iTestHelperErrorCount;

void Assertion(bool p_bCondition, int p_iLine, const char* p_pcFilename, const char* p_pcMessage = "");

#define Assertion(condition, message) Assertion(condition, __LINE__, __FILE__, message)

