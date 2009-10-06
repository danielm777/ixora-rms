/*
* Created with help from Mike Muuss' PING.C
* (part of netkit-base-0.17)
*   AND
* Help from Beej's guide to network programming
* http://www.ecst.csuchico.edu/~beej/guide/net/
*/

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>

#include <map>

#include <sys/types.h>
#include <sys/timeb.h>

////////////////////////////////// linux
#ifndef _WIN32

#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <netinet/ip.h>

#else
////////////////////////////////// windows

typedef unsigned char		u_int8_t;
typedef unsigned short int	u_int16_t;
#include <winsock2.h>
#include <ws2tcpip.h>

/*
 * For Windows need to initialize and cleanup winsock2
 */
class Init
{
public:
	Init()
	{
		WSADATA	data;
		WSAStartup(MAKEWORD( 2, 2), &data);
	}

	~Init()
	{
		WSACleanup();
	}
};
Init init;

#endif

//////////////////////////////////

#include <jni.h>
extern "C" {
	/*
	 * Class:     com_ixora_common_net_icmp_ICMP
	 * Method:    icmpSend
	 * Signature: (Ljava/lang/String;ILjava/lang/String;)I
	 */
	JNIEXPORT jint JNICALL _Java_com_ixora_common_net_icmp_ICMP_icmpSend
	  (JNIEnv *, jobject, jstring, jint, jstring);

	/*
	 * Class:     com_ixora_common_net_icmp_ICMP
	 * Method:    icmpRecv
	 * Signature: (I)Ljava/lang/String;
	 */
	JNIEXPORT jstring JNICALL _Java_com_ixora_common_net_icmp_ICMP_icmpRecv
	  (JNIEnv *, jobject, jint);

	/*
	 * Class:     com_ixora_common_net_icmp_ICMP
	 * Method:    ping
	 * Signature: (Ljava/lang/String;II)I
	 */
	JNIEXPORT jint JNICALL _Java_com_ixora_common_net_icmp_ICMP_ping
	  (JNIEnv *, jclass, jstring, jint, jint);
}


//////////////////////////////////
/*
 * Deals with compilers which generate different little/big endians
 */
#ifndef LITTLE_ENDIAN
#define LITTLE_ENDIAN   1234
#define BIG_ENDIAN      4321
#endif
#ifndef BYTE_ORDER
#define BYTE_ORDER      LITTLE_ENDIAN
#endif

#pragma pack(1)
struct ip
{
#if BYTE_ORDER == LITTLE_ENDIAN
	u_int   ip_hl:4,                /* header length */
			ip_v:4;                 /* version */
#endif
#if BYTE_ORDER == BIG_ENDIAN
	u_int   ip_v:4,                 /* version */
			ip_hl:4;                /* header length */
#endif
	u_char  ip_tos;                 /* type of service */
	u_short ip_len;                 /* total length */
	u_short ip_id;                  /* identification */
	u_short ip_off;                 /* fragment offset field */
	u_char  ip_ttl;                 /* time to live */
	u_char  ip_p;                   /* protocol */
	u_short ip_sum;                 /* checksum */
	in_addr ip_src,ip_dst;  /* source and dest address */
};


struct icmp_echo
{
	u_int8_t	type;
	u_int8_t	code;
	u_int16_t	checksum;
	u_int16_t	identifier;
	u_int16_t	sequence_number;
};
#pragma pack()

/*
* IP header checksum code: http://www.fenix.ne.jp/~thomas/memo/ip/checksum.html
*/
unsigned short checksum(unsigned short *buf, int size)
{
    unsigned long sum = 0;

    while (size > 1) {
		sum += *buf++;
		size -= 2;
    }

    if (size) {
		sum += *(u_int8_t *)buf;
    }

    sum = (sum & 0xffff) + (sum >> 16);
    sum = (sum & 0xffff) + (sum >> 16);

    return ~sum;
}

//////////////////////////////////////////////////////////////////////////////
// Class ICMP

#define INPACK_LENGTH 65535
#define OUTPACK_LENGTH 65535

class ICMP
{
protected:
	pid_t		ident;
	u_int16_t	seq;
	int			sockfd_global;
	int			invalid_checksum;

public:
	ICMP()
	{
		ident = 0;
		seq = 0;
		invalid_checksum = 0;
	}

	int send_echo_request(JNIEnv *env, int sockfd, sockaddr_in *their_addr, const char *data)
	{
		char		outpack[OUTPACK_LENGTH];
		size_t		space = 0;
		int			numbytes = 0;
		icmp_echo	echo_packet;

		echo_packet.type = 8;
		echo_packet.code = 0;
		echo_packet.checksum = 0;
		echo_packet.identifier = (u_int16_t) getpid();
		echo_packet.sequence_number = seq;

		memcpy(outpack, &echo_packet, sizeof(icmp_echo));

		space += sizeof(icmp_echo);

		memcpy(outpack+(sizeof(icmp_echo)), data, strlen(data));

		space += strlen(data);

		invalid_checksum = 0;

		echo_packet.checksum = checksum((unsigned short *)outpack, space);

		memcpy(outpack, &echo_packet, sizeof(icmp_echo));

		numbytes = sendto(sockfd, outpack, space, 0,
			(sockaddr *)their_addr, sizeof(sockaddr));

		if (numbytes == -1)
			return -4;

		return 0;
	}

	/*
	* int icmp_echo_send(char *host, int sequence, char *data);
	*
	* Parameters:
	*    host     - host to send icmp echo packet to
	*    sequence - packet sequence number
	*    data     - data to include in packet
	*
	* Return Values:
	*    0        - icmp echo send success
	*    -1       - failure looking up hostname
	*    -2       - failure creating network socket
	*    -3       - error malloc'ing memory
	*    -4       - error sending packet
	*/

	int icmp_echo_send(JNIEnv *env, hostent *he, int sequence, const char *data)
	{
		int sockfd;
		sockaddr_in dest;

		sockfd = socket(PF_INET, SOCK_RAW, 1);
		sockfd_global = sockfd;

		if (sockfd == -1)
			return -2;

		dest.sin_family = PF_INET;
		dest.sin_port = htons(0);
		dest.sin_addr = *((in_addr *)he->h_addr);
		memset(&(dest.sin_zero), '\0', 8);

		ident = getpid();
		seq = sequence;

		return send_echo_request(env, sockfd, &dest, data);
	}

	int icmp_reply(JNIEnv *env, char *inpack, int packlen, char* icmp_data)
	{
		ip			*ip_pkt;
		icmp_echo	*icmp_pkt;

		size_t ipoptlen;
		size_t hlen;

		u_int16_t cksum, cksum_tmp;
		size_t space = 0;

	// Extract header information
		ip_pkt = (ip *)inpack;

		hlen = ip_pkt->ip_hl << 2;
		ipoptlen = hlen - sizeof(ip);
		packlen -= hlen;

		icmp_pkt = (icmp_echo *) (inpack+hlen);

		if ((u_int16_t) getpid() != icmp_pkt->identifier)
			return -1;

		char *data = (char*)malloc(INPACK_LENGTH);
		memcpy(data, inpack+hlen+8, packlen-8);

	// Do a checksum on the header and data
		cksum = icmp_pkt->checksum;
		icmp_pkt->checksum = 0;

		char *tmp_ck = (char*)malloc(OUTPACK_LENGTH);
		memcpy(tmp_ck, icmp_pkt, sizeof(icmp_echo));
		space += sizeof(icmp_echo);

		memcpy(tmp_ck+(sizeof(icmp_echo)), data, strlen(data));
		space += strlen(data);

		cksum_tmp = checksum((unsigned short *) tmp_ck, space);

		if (cksum_tmp != cksum)
			invalid_checksum = 1;

		icmp_pkt->checksum = cksum;

	// Return
		sprintf(icmp_data, "%d %s\0", icmp_pkt->sequence_number, data);
		free(tmp_ck);
		free(data);

		return 0;
	}

	int icmp_echo_reply(JNIEnv *env, int timeout, char* icmp_data)
	{
		int packlen = 0, retval = 0;

		char inpack[INPACK_LENGTH];

		fd_set		readfds;
		timeval		tv;
		sockaddr_in from;
		socklen_t	fromlen = sizeof(from);
		int			sockfd = sockfd_global;

		FD_ZERO(&readfds);
		FD_SET(sockfd, &readfds);

		tv.tv_sec = timeout;
		tv.tv_usec = 0;

		while (tv.tv_sec > 0)
		{
			retval = select(sockfd + 1, &readfds, NULL, NULL, &tv);

			if (retval > 0)
			{
				packlen = recvfrom(sockfd, inpack, INPACK_LENGTH, 0,
					(sockaddr *)&from, &fromlen);
				if ((icmp_reply(env, inpack, packlen, icmp_data)) == -1)
					continue; // not our packet
				else
					return 0;
			}
			else
				return -1;
		}
	}
};

/*
 * This is a map between Java and C++ objects, in order to call multiple send and recv
 * from Java on the same objects here in C++
 */
class MapICMPs : public std::map<jobject, ICMP*>
{
public:

// 'obj' is the 'this' pointer in the java class. If this object has been
// called before it will be in our map. Otherwise create a new entry.
	ICMP* getICMPObject(jobject obj)
	{
	// TODO: synchronize this code from here ...
		ICMP* pThis = NULL;
		MapICMPs::iterator it = find(obj);
		if (it != end())
			pThis = it->second;
		else
		{
			pThis = new ICMP();
			insert(std::make_pair(obj, pThis));
		}

	// ... to here

		return pThis;
	}
};

MapICMPs	mapICMPs;


/*
* JNI Wrapper code to icmp_echo_send()
*/
JNIEXPORT jint JNICALL _Java_com_ixora_common_net_icmp_ICMP_icmpSend (
		JNIEnv *env, jobject obj, jstring host, jint s, jstring data)
{
	jint retval = 0;
	jobject thisObject = env->GetObjectField(obj, NULL); // get the value of 'this'

// Make the call to the object associated to the 'this' pointer in the java class
	ICMP* pThis = mapICMPs.getICMPObject(thisObject);
	if (pThis)
	{
		const char *h = env->GetStringUTFChars(host, 0);
		const char *d = env->GetStringUTFChars(data, 0);

	// Verify host name
		hostent *he = gethostbyname(h);
		if (he != NULL)
		{
			retval = (jint) pThis->icmp_echo_send(env, he, s, d);
			if (retval == -2 || retval == -4)
				env->ThrowNew(env->FindClass("java/net/SocketException"), "");
		}
		else
			env->ThrowNew(env->FindClass("java/net/UnknownHostException"), "");

		env->ReleaseStringUTFChars(host, h);
		env->ReleaseStringUTFChars(data, d);
	}

	return retval;
}

/*
* JNI Wrapper code to icmp_echo_reply()
* (returns NULL if a error occours receiving the packet,
* or packet timeout expires)
*/

JNIEXPORT jstring JNICALL _Java_com_ixora_common_net_icmp_ICMP_icmpRecv (
	JNIEnv *env, jobject obj, jint t)
{
    jstring tmp = NULL;
	jobject thisObject = env->GetObjectField(obj, NULL); // get the value of 'this'

// Make the call to the object associated to the 'this' pointer in the java class
	ICMP* pThis = mapICMPs.getICMPObject(thisObject);
	if (pThis)
	{
		char icmp_data[INPACK_LENGTH];

		int retval = pThis->icmp_echo_reply(env, t, icmp_data);

	// Throw a Java exception in case of timeout
		if (retval < 0)
		{
			env->ThrowNew(env->FindClass("java/net/SocketTimeoutException"), "");
			return NULL;
		}

		tmp = env->NewStringUTF(icmp_data);
	}

    return tmp;
}

/*
* JNI Wrapper code to ping()
*/

JNIEXPORT jint JNICALL _Java_com_ixora_common_net_icmp_ICMP_ping
  (JNIEnv *env, jclass obj, jstring host, jint bytes, jint timeout)
{
	ICMP	icmp;
	const char *h = env->GetStringUTFChars(host, 0);

// Max length buffers for in/out data
	char out_data[OUTPACK_LENGTH], in_data[INPACK_LENGTH];
	memset(out_data, 0, OUTPACK_LENGTH);
	memset(in_data, 0, INPACK_LENGTH);

// Fill with string data
	if (bytes > OUTPACK_LENGTH - 1000)
		bytes = OUTPACK_LENGTH - 1000;
	memset(out_data, '0', bytes);

	int	retval = 0;
	timeb start;
	timeb end;

// gethostbyname is expensive, do not include it in the timed section
	hostent *he = gethostbyname(h);
	env->ReleaseStringUTFChars(host, h);

	if (he != NULL)
	{
		ftime(&start);
// Send the request for echo
		retval = icmp.icmp_echo_send(env, he, bytes, out_data);
		if (retval == -2 || retval == -4)
		{
			env->ThrowNew(env->FindClass("java/net/SocketException"), "");
			return 0;
		}
	}
	else
	{
		env->ThrowNew(env->FindClass("java/net/UnknownHostException"), "");
		return 0;
	}

// Wait for a reply
	retval = icmp.icmp_echo_reply(env, timeout, in_data);
	ftime(&end);

// Throw a Java exception in case of timeout
	if (retval < 0)
	{
		env->ThrowNew(env->FindClass("java/net/SocketTimeoutException"), "");
		return 0;
	}

	return end.time * 1000 + end.millitm - start.time * 1000 - start.millitm;
}
