# Addr

Addr collects ASN and City Databases from [IpToAsn](https://iptoasn.com), [DB-IP](https://db-ip.com),
[Firehol](https://iplists.firehol.org/), [geonames](http://www.geonames.org/), and [Lukes ISO-3166](https://github.com/lukes/ISO-3166-Countries-with-Regional-Codes/)
and transforms them into filtered and more expressive way.
</br>
ASN: Addr contains separate database for ASN which points to Public Cloud Provider.
</br>
City: Addr contain database for City IP Address which has all attributes of DB-IP.com data, but it also contains Country name and Continent name.
</br>

## Files:

generated/ASN.csv = Mirror of DB-IP.com ASN Lite
</br>
generated/CloudASN.json = ASN of Public Cloud Providers
</br>
generated/City.json = Extended Database of DB-IP.com City Lite

### Powered By:
<a href='https://db-ip.com'>IP Geolocation by DB-IP</a>
