# mas_assignment

For the following task students should build an Android application that will:

1. download in parallel the following network resources:

https://student.ism.ase.ro/access/content/group/Y2S1_MAS/AndroidEncrypt/AES_IV_encrypted_with_RSA_PrivateKey
https://student.ism.ase.ro/access/content/group/Y2S1_MAS/AndroidEncrypt/AES_Key_encrypted_with_RSA_PrivateKey
https://student.ism.ase.ro/access/content/group/Y2S1_MAS/AndroidEncrypt/Image_encrypted_with_AES
https://student.ism.ase.ro/access/content/group/Y2S1_MAS/AndroidEncrypt/RSA_PublicKey
2. use the RSA public key to decrypt the AES secret and IV;

3. use an AES cipher to decrypt the image;

4. display the image in an image view;