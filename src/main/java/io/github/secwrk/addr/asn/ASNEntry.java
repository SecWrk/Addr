/*
 * Copyright 2022, SecWrk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.secwrk.addr.asn;

import inet.ipaddr.IPAddress;
import inet.ipaddr.format.IPAddressRange;
import inet.ipaddr.ipv4.IPv4Address;
import inet.ipaddr.ipv4.IPv4AddressSeqRange;
import inet.ipaddr.ipv6.IPv6Address;
import inet.ipaddr.ipv6.IPv6AddressSeqRange;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

/**
 * {@link ASNEntry} holds information of ASN.
 */
public final class ASNEntry {

    private final String ipRangeStart;
    private final String ipRangeEnd;
    private final long asn;
    private final String countryCode;
    private final String aso;

    private final IPAddressRange ipAddressRange;

    private ASNEntry(String ipRangeStart, String ipRangeEnd, long asn, String countryCode, String aso) throws UnknownHostException {
        this.ipRangeStart = Objects.requireNonNull(ipRangeStart, "IpRangeStart");
        this.ipRangeEnd = Objects.requireNonNull(ipRangeEnd, "IpRangeEnd");
        this.asn = asn;
        this.countryCode = Objects.requireNonNull(countryCode, "CountryCode");
        this.aso = Objects.requireNonNull(aso, "Aso");

        InetAddress start = InetAddress.getByName(ipRangeStart);
        InetAddress end = InetAddress.getByName(ipRangeEnd);

        if (start instanceof Inet4Address && end instanceof Inet4Address) {
            ipAddressRange = new IPv4AddressSeqRange(new IPv4Address((Inet4Address) start), new IPv4Address((Inet4Address) end));
        } else if (start instanceof Inet6Address && end instanceof Inet6Address) {
            ipAddressRange = new IPv6AddressSeqRange(new IPv6Address((Inet6Address) start), new IPv6Address((Inet6Address) end));
        } else {
            throw new IllegalArgumentException("Invalid IP Address Range: " + start + "-" + end);
        }
    }

    private ASNEntry(String ipRangeStart, String ipRangeEnd, long asn, String aso) {
        this.ipRangeStart = Objects.requireNonNull(ipRangeStart, "IpRangeStart");
        this.ipRangeEnd = Objects.requireNonNull(ipRangeEnd, "IpRangeEnd");
        this.asn = asn;
        this.aso = Objects.requireNonNull(aso, "Aso");

        countryCode = null;
        ipAddressRange = null;
    }

    /**
     * Create a new {@link ASNEntry} instance
     *
     * @param ipRangeStart Start of range
     * @param ipRangeEnd   End of range
     * @param asn          Autonomous System Number
     * @param aso          Autonomous System Organization
     * @return {@link ASNEntry} instance
     */
    public static ASNEntry create(String ipRangeStart, String ipRangeEnd, long asn, String aso) {
        return new ASNEntry(ipRangeStart, ipRangeEnd, asn, aso);
    }

    /**
     * Create a new {@link ASNEntry} instance
     *
     * @param ipRangeStart Start of range
     * @param ipRangeEnd   End of range
     * @param asn          Autonomous System Number
     * @param countryCode  Country code
     * @param aso          Autonomous System Organization
     * @return {@link ASNEntry} instance
     * @throws UnknownHostException If range is invalid
     */
    public static ASNEntry create(String ipRangeStart, String ipRangeEnd, long asn, String countryCode, String aso) throws UnknownHostException {
        return new ASNEntry(ipRangeStart, ipRangeEnd, asn, countryCode, aso);
    }

    /**
     * Returns {@link Boolean#TRUE} if this IP address range is non-routable
     */
    public boolean isNonRoutable() {
        return asn == 0 && countryCode.equalsIgnoreCase("None") &&
                aso.equalsIgnoreCase("Not routed");
    }

    /**
     * Start of IP range
     */
    public String ipRangeStart() {
        return ipRangeStart;
    }

    /**
     * End of IP range
     */
    public String ipRangeEnd() {
        return ipRangeEnd;
    }

    /**
     * IP address range
     */
    public IPAddressRange ipAddressRange() {
        return ipAddressRange;
    }

    /**
     * Returns {@link Boolean#TRUE} if an IP address is within range else
     * {@link Boolean#FALSE}
     */
    public boolean isInsideRange(IPAddress address) {
        return ipAddressRange.contains(address);
    }

    /**
     * Autonomous system number
     */
    public long asn() {
        return asn;
    }

    /**
     * Country Code
     */
    public String countryCode() {
        return countryCode;
    }

    /**
     * Autonomous system organization
     */
    public String aso() {
        return aso;
    }

    @Override
    public String toString() {
        return "IpEntry{" +
                "ipRangeStart='" + ipRangeStart + '\'' +
                ", ipRangeEnd='" + ipRangeEnd + '\'' +
                ", asn=" + asn +
                ", countryCode='" + countryCode + '\'' +
                ", aso='" + aso + '\'' +
                ", ipAddressRange=" + ipAddressRange +
                '}';
    }
}
