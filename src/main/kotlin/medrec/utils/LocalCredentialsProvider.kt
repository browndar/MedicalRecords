package medrec.utils

import com.google.api.gax.core.CredentialsProvider
import com.google.auth.Credentials

class LocalCredentialsProvider : CredentialsProvider {
    override fun getCredentials(): Credentials {
        val creds =  Utils.INSTANCE.getCredentials()

        

        return creds
    }

}